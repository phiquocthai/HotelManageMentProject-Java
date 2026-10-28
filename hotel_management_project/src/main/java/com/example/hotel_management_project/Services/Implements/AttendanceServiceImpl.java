package com.example.hotel_management_project.Services.Implements;

import com.example.hotel_management_project.Converter.AttendanceConverter;
import com.example.hotel_management_project.DTO.CustomException;
import com.example.hotel_management_project.DTO.Request.AttendanceRequest;
import com.example.hotel_management_project.DTO.Response.AttendanceResponse;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.Models.Entity.Attendance;
import com.example.hotel_management_project.Models.Entity.ShiftAssignment;
import com.example.hotel_management_project.Models.Enums.AttendanceStatus;
import com.example.hotel_management_project.Models.Enums.ErrorCode;
import com.example.hotel_management_project.Repository.AttendanceRepo;
import com.example.hotel_management_project.Repository.ShiftAssignmentRepo;
import com.example.hotel_management_project.Services.AttendanceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepo attendanceRepo;
    private final ShiftAssignmentRepo shiftAssignmentRepo;
    private final AttendanceConverter attendanceConverter;

    public AttendanceServiceImpl(AttendanceRepo attendanceRepo,
                                 ShiftAssignmentRepo shiftAssignmentRepo,
                                 AttendanceConverter attendanceConverter) {
        this.attendanceRepo = attendanceRepo;
        this.shiftAssignmentRepo = shiftAssignmentRepo;
        this.attendanceConverter = attendanceConverter;
    }

    @Override
    public AttendanceResponse createAttendance(AttendanceRequest request) {
        ShiftAssignment shiftAssignment = shiftAssignmentRepo.findById(request.getShiftAssignmentId())
                .orElseThrow(() -> new CustomException(ErrorCode.SHIFT_ASSIGNMENT_NOT_FOUND));

        if (attendanceRepo.existsByShiftAssignmentId(request.getShiftAssignmentId())) {
            throw new CustomException(ErrorCode.ATTENDANCE_EXISTED);
        }

        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_ATTENDANCE_STATUS);
        }

        try {
            AttendanceStatus.fromLabel(request.getStatus());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_ATTENDANCE_STATUS);
        }

        if (request.getCheckInTime() != null && request.getCheckOutTime() != null
                && request.getCheckOutTime().isBefore(request.getCheckInTime())) {
            throw new CustomException(ErrorCode.INVALID_ATTENDANCE_TIME);
        }

        Attendance attendance = attendanceConverter.toAttendanceEntity(request, shiftAssignment);
        attendance = attendanceRepo.save(attendance);
        return attendanceConverter.toAttendanceResponse(attendance);
    }

    @Override
    public AttendanceResponse updateAttendance(Long id, AttendanceRequest request) {
        // Bước 1: Kiểm tra bản ghi chấm công có tồn tại trong DB theo ID hay không
        Attendance existing = attendanceRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ATTENDANCE_NOT_FOUND));

        // Bước 2: Xử lý khi có sự thay đổi về ca làm việc (shiftAssignmentId)
        ShiftAssignment shiftAssignment = existing.getShiftAssignment();
        if (request.getShiftAssignmentId() != null
                && !existing.getShiftAssignment().getId().equals(request.getShiftAssignmentId())) {
            // 2.1: Kiểm tra ca làm việc mới có tồn tại trong hệ thống không
            shiftAssignment = shiftAssignmentRepo.findById(request.getShiftAssignmentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.SHIFT_ASSIGNMENT_NOT_FOUND));

            // 2.2: Vì quan hệ là 1-1, kiểm tra xem ca làm việc mới này đã có bản ghi chấm công nào khác chưa
            if (attendanceRepo.existsByShiftAssignmentId(request.getShiftAssignmentId())) {
                throw new CustomException(ErrorCode.ATTENDANCE_EXISTED);
            }
        }

        // Bước 3: Kiểm tra tính hợp lệ của trạng thái chấm công (phải thuộc danh mục: "Đủ công", "Đi trễ", "Về sớm", "Vắng mặt")
        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            try {
                AttendanceStatus.fromLabel(request.getStatus());
            } catch (IllegalArgumentException e) {
                throw new CustomException(ErrorCode.INVALID_ATTENDANCE_STATUS);
            }
        }

        // Bước 4: Kiểm tra tính logic của thời gian (giờ ra checkOutTime không được trước giờ vào checkInTime)
        if (request.getCheckInTime() != null && request.getCheckOutTime() != null
                && request.getCheckOutTime().isBefore(request.getCheckInTime())) {
            throw new CustomException(ErrorCode.INVALID_ATTENDANCE_TIME);
        }

        // Bước 5: Ánh xạ dữ liệu mới từ request vào entity hiện tại và lưu xuống Database
        attendanceConverter.updateEntity(request, existing, shiftAssignment);
        existing = attendanceRepo.save(existing);

        // Bước 6: Chuyển đổi entity đã cập nhật sang AttendanceResponse để trả về cho Client
        return attendanceConverter.toAttendanceResponse(existing);
    }

    @Override
    public PageResponse<AttendanceResponse> getAllAttendances(Pageable pageable) {
        // Attendance không có trường active nên lấy toàn bộ (findAll)
        Page<Attendance> page = attendanceRepo.findAll(pageable);
        List<AttendanceResponse> content = page.getContent().stream()
                .map(attendanceConverter::toAttendanceResponse)
                .collect(Collectors.toList());

        return PageResponse.<AttendanceResponse>builder()
                .pageNo(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .content(content)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AttendanceResponse> searchAttendances(
            String staffName, Long workShiftId, LocalDate workDate, AttendanceStatus status, Pageable pageable) {
        String trimmedStaff = (staffName != null && !staffName.trim().isEmpty()) ? staffName.trim() : null;
        Page<Attendance> page = attendanceRepo.searchAttendances(
                trimmedStaff, workShiftId, workDate, status, pageable);
        List<AttendanceResponse> content = page.getContent().stream()
                .map(attendanceConverter::toAttendanceResponse)
                .collect(Collectors.toList());

        return PageResponse.<AttendanceResponse>builder()
                .pageNo(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .content(content)
                .build();
    }

    @Override
    public List<AttendanceResponse> getAllAttendances() {
        return attendanceRepo.findAll().stream()
                .map(attendanceConverter::toAttendanceResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAttendance(Long id) {
        Attendance attendance = attendanceRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ATTENDANCE_NOT_FOUND));

        // Attendance không có trường active nên thực hiện xóa cứng
        attendanceRepo.delete(attendance);
    }
}
