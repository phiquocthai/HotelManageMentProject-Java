package com.example.hotel_management_project.Services.Implements;

import com.example.hotel_management_project.Converter.ShiftAssignmentConverter;
import com.example.hotel_management_project.DTO.CustomException;
import com.example.hotel_management_project.DTO.Request.ShiftAssignmentRequest;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.DTO.Response.ShiftAssignmentResponse;
import com.example.hotel_management_project.Models.Entity.ShiftAssignment;
import com.example.hotel_management_project.Models.Entity.Staff;
import com.example.hotel_management_project.Models.Entity.WorkShift;
import com.example.hotel_management_project.Models.Enums.ErrorCode;
import com.example.hotel_management_project.Repository.AttendanceRepo;
import com.example.hotel_management_project.Repository.ShiftAssignmentRepo;
import com.example.hotel_management_project.Repository.StaffRepo;
import com.example.hotel_management_project.Repository.WorkShiftRepo;
import com.example.hotel_management_project.Services.ShiftAssignmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShiftAssignmentServiceImpl implements ShiftAssignmentService {

    private final ShiftAssignmentRepo shiftAssignmentRepo;
    private final StaffRepo staffRepo;
    private final WorkShiftRepo workShiftRepo;
    private final AttendanceRepo attendanceRepo;
    private final ShiftAssignmentConverter shiftAssignmentConverter;

    public ShiftAssignmentServiceImpl(ShiftAssignmentRepo shiftAssignmentRepo,
                                     StaffRepo staffRepo,
                                     WorkShiftRepo workShiftRepo,
                                     AttendanceRepo attendanceRepo,
                                     ShiftAssignmentConverter shiftAssignmentConverter) {
        this.shiftAssignmentRepo = shiftAssignmentRepo;
        this.staffRepo = staffRepo;
        this.workShiftRepo = workShiftRepo;
        this.attendanceRepo = attendanceRepo;
        this.shiftAssignmentConverter = shiftAssignmentConverter;
    }

    @Override
    @Transactional
    public ShiftAssignmentResponse createShiftAssignment(ShiftAssignmentRequest request) {
        // Bước 1: Kiểm tra nhân viên có tồn tại trong hệ thống không
        Staff staff = staffRepo.findById(request.getStaffId())
                .orElseThrow(() -> new CustomException(ErrorCode.STAFF_NOT_FOUND));

        // Bước 2: Kiểm tra ca làm việc có tồn tại trong hệ thống không
        WorkShift workShift = workShiftRepo.findById(request.getWorkShiftId())
                .orElseThrow(() -> new CustomException(ErrorCode.WORK_SHIFT_NOT_FOUND));

        // Bước 3: Kiểm tra ràng buộc duy nhất (1 nhân viên không được phân 2 lần vào cùng 1 ca trong 1 ngày)
        if (shiftAssignmentRepo.existsByStaffIdAndWorkShiftIdAndWorkDate(
                staff.getId(), workShift.getId(), request.getWorkDate())) {
            throw new CustomException(ErrorCode.SHIFT_ASSIGNMENT_EXISTED);
        }

        // Bước 4: Chuyển đổi sang entity và lưu xuống Database
        ShiftAssignment shiftAssignment = shiftAssignmentConverter.toShiftAssignmentEntity(request, staff, workShift);
        shiftAssignment = shiftAssignmentRepo.save(shiftAssignment);

        // Bước 5: Chuyển đổi sang Response DTO (chỉ có duy nhất 1 ID) và trả về
        return shiftAssignmentConverter.toShiftAssignmentResponse(shiftAssignment);
    }

    @Override
    @Transactional
    public ShiftAssignmentResponse updateShiftAssignment(Long id, ShiftAssignmentRequest request) {
        // Bước 1: Kiểm tra bản ghi phân ca có tồn tại theo ID hay không
        ShiftAssignment existing = shiftAssignmentRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.SHIFT_ASSIGNMENT_NOT_FOUND));

        // Bước 2: Kiểm tra thông tin nhân viên
        Staff staff = existing.getStaff();
        if (!existing.getStaff().getId().equals(request.getStaffId())) {
            staff = staffRepo.findById(request.getStaffId())
                    .orElseThrow(() -> new CustomException(ErrorCode.STAFF_NOT_FOUND));
        }

        // Bước 3: Kiểm tra thông tin ca làm việc
        WorkShift workShift = existing.getWorkShift();
        if (!existing.getWorkShift().getId().equals(request.getWorkShiftId())) {
            workShift = workShiftRepo.findById(request.getWorkShiftId())
                    .orElseThrow(() -> new CustomException(ErrorCode.WORK_SHIFT_NOT_FOUND));
        }

        // Bước 4: Nếu có sự thay đổi về (nhân viên, ca, ngày làm việc), kiểm tra xem tổ hợp mới có bị trùng không
        boolean infoChanged = !existing.getStaff().getId().equals(staff.getId())
                || !existing.getWorkShift().getId().equals(workShift.getId())
                || !existing.getWorkDate().equals(request.getWorkDate());

        if (infoChanged && shiftAssignmentRepo.existsByStaffIdAndWorkShiftIdAndWorkDate(
                staff.getId(), workShift.getId(), request.getWorkDate())) {
            throw new CustomException(ErrorCode.SHIFT_ASSIGNMENT_EXISTED);
        }

        // Bước 5: Cập nhật entity và lưu xuống Database
        shiftAssignmentConverter.updateEntity(request, existing, staff, workShift);
        existing = shiftAssignmentRepo.save(existing);

        // Bước 6: Chuyển đổi sang Response DTO và trả về kết quả
        return shiftAssignmentConverter.toShiftAssignmentResponse(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ShiftAssignmentResponse> getAllShiftAssignments(Pageable pageable) {
        // ShiftAssignment không có trường active nên lấy toàn bộ danh sách phân trang (findAll)
        Page<ShiftAssignment> page = shiftAssignmentRepo.findAll(pageable);
        List<ShiftAssignmentResponse> content = page.getContent().stream()
                .map(shiftAssignmentConverter::toShiftAssignmentResponse)
                .collect(Collectors.toList());

        return PageResponse.<ShiftAssignmentResponse>builder()
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
    public PageResponse<ShiftAssignmentResponse> searchShiftAssignments(
            String staffName, Long workShiftId, LocalDate workDate, Pageable pageable) {
        // Bước 1: Chuẩn hóa từ khóa tìm kiếm tên nhân viên (cắt khoảng trắng thừa, nếu để trống thì gán null)
        String trimmedStaff = (staffName != null && !staffName.trim().isEmpty()) ? staffName.trim() : null;

        // Bước 2: Thực hiện truy vấn tìm kiếm phân trang theo các điều kiện lọc (tên nhân viên, ca làm, ngày làm việc)
        Page<ShiftAssignment> page = shiftAssignmentRepo.searchShiftAssignments(
                trimmedStaff, workShiftId, workDate, pageable);

        // Bước 3: Chuyển đổi danh sách Entity sang Response DTO
        List<ShiftAssignmentResponse> content = page.getContent().stream()
                .map(shiftAssignmentConverter::toShiftAssignmentResponse)
                .collect(Collectors.toList());

        // Bước 4: Đóng gói kết quả tìm kiếm vào PageResponse kèm thông tin phân trang (trang hiện tại, tổng trang, tổng số bản ghi)
        return PageResponse.<ShiftAssignmentResponse>builder()
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
    public List<ShiftAssignmentResponse> getAllShiftAssignments() {
        return shiftAssignmentRepo.findAll().stream()
                .map(shiftAssignmentConverter::toShiftAssignmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteShiftAssignment(Long id) {
        // Bước 1: Kiểm tra bản ghi phân ca có tồn tại theo ID hay không
        ShiftAssignment existing = shiftAssignmentRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.SHIFT_ASSIGNMENT_NOT_FOUND));

        // Bước 2: Dọn dẹp bản ghi chấm công đính kèm (nếu có) trước để tránh lỗi ràng buộc khóa ngoại
        attendanceRepo.findByShiftAssignmentId(id).ifPresent(attendanceRepo::delete);

        // Bước 3: ShiftAssignment không có trường active nên thực hiện xóa cứng
        shiftAssignmentRepo.delete(existing);
    }
}
