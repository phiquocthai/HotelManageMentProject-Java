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
import com.example.hotel_management_project.Repository.ShiftAssignmentRepo;
import com.example.hotel_management_project.Repository.StaffRepo;
import com.example.hotel_management_project.Repository.WorkShiftRepo;
import com.example.hotel_management_project.Services.ShiftAssignmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShiftAssignmentServiceImpl implements ShiftAssignmentService {

    private ShiftAssignmentRepo shiftAssignmentRepo;
    private ShiftAssignmentConverter shiftAssignmentConverter;
    private StaffRepo staffRepo;
    private WorkShiftRepo workShiftRepo;

    public ShiftAssignmentServiceImpl(ShiftAssignmentRepo shiftAssignmentRepo,
                                     ShiftAssignmentConverter shiftAssignmentConverter,
                                     StaffRepo staffRepo,
                                     WorkShiftRepo workShiftRepo) {
        this.shiftAssignmentRepo = shiftAssignmentRepo;
        this.shiftAssignmentConverter = shiftAssignmentConverter;
        this.staffRepo = staffRepo;
        this.workShiftRepo = workShiftRepo;
    }

    @Override
    public PageResponse<ShiftAssignmentResponse> getAllShiftAssignments(Pageable pageable) {
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
    public ShiftAssignmentResponse createShiftAssignment(ShiftAssignmentRequest shiftAssignmentRequest) {
        // Kiểm tra tính hợp lệ và chống trùng lịch
        checkShiftAssignment(shiftAssignmentRequest);

        Staff staff = staffRepo.findById(shiftAssignmentRequest.getStaffId())
                .orElseThrow(() -> new CustomException(ErrorCode.STAFF_NOT_FOUND));

        WorkShift workShift = workShiftRepo.findById(shiftAssignmentRequest.getWorkShiftId())
                .orElseThrow(() -> new CustomException(ErrorCode.WORK_SHIFT_NOT_FOUND));

        ShiftAssignment shiftAssignment = shiftAssignmentConverter.toShiftAssignmentEntity(shiftAssignmentRequest);
        shiftAssignment.setStaff(staff);
        shiftAssignment.setWorkShift(workShift);

        ShiftAssignment savedAssignment = shiftAssignmentRepo.save(shiftAssignment);
        return shiftAssignmentConverter.toShiftAssignmentResponse(savedAssignment);
    }

    @Override
    public ShiftAssignmentResponse updateShiftAssignment(Long id, ShiftAssignmentRequest shiftAssignmentRequest) {
        ShiftAssignment existingAssignment = shiftAssignmentRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.SHIFT_ASSIGNMENT_NOT_FOUND));

        Staff staff = staffRepo.findById(shiftAssignmentRequest.getStaffId())
                .orElseThrow(() -> new CustomException(ErrorCode.STAFF_NOT_FOUND));

        WorkShift workShift = workShiftRepo.findById(shiftAssignmentRequest.getWorkShiftId())
                .orElseThrow(() -> new CustomException(ErrorCode.WORK_SHIFT_NOT_FOUND));

        // Kiểm tra xem phân ca mới có bị trùng với một bản ghi khác không
        boolean isDuplicate = shiftAssignmentRepo.existsByStaffIdAndWorkShiftIdAndWorkDate(
                shiftAssignmentRequest.getStaffId(),
                shiftAssignmentRequest.getWorkShiftId(),
                shiftAssignmentRequest.getWorkDate()
        );

        boolean isSameRecord = existingAssignment.getStaff().getId().equals(shiftAssignmentRequest.getStaffId())
                && existingAssignment.getWorkShift().getId().equals(shiftAssignmentRequest.getWorkShiftId())
                && existingAssignment.getWorkDate().equals(shiftAssignmentRequest.getWorkDate());

        if (isDuplicate && !isSameRecord) {
            throw new CustomException(ErrorCode.SHIFT_ASSIGNMENT_EXISTED);
        }

        existingAssignment.setStaff(staff);
        existingAssignment.setWorkShift(workShift);
        existingAssignment.setWorkDate(shiftAssignmentRequest.getWorkDate());

        ShiftAssignment updatedAssignment = shiftAssignmentRepo.save(existingAssignment);
        return shiftAssignmentConverter.toShiftAssignmentResponse(updatedAssignment);
    }

    @Override
    public void deleteShiftAssignmentById(Long id) {
        ShiftAssignment shiftAssignment = shiftAssignmentRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.SHIFT_ASSIGNMENT_NOT_FOUND));
        
        shiftAssignmentRepo.delete(shiftAssignment);
    }

    public void checkShiftAssignment(ShiftAssignmentRequest shiftAssignmentRequest) {
        // Kiểm tra ràng buộc duy nhất (UniqueConstraint): 1 nhân viên, 1 ca, 1 ngày
        if (shiftAssignmentRepo.existsByStaffIdAndWorkShiftIdAndWorkDate(
                shiftAssignmentRequest.getStaffId(),
                shiftAssignmentRequest.getWorkShiftId(),
                shiftAssignmentRequest.getWorkDate())) {
            throw new CustomException(ErrorCode.SHIFT_ASSIGNMENT_EXISTED);
        }
    }
}