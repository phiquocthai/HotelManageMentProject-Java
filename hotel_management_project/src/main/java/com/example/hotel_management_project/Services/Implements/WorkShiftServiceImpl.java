package com.example.hotel_management_project.Services.Implements;

import com.example.hotel_management_project.Converter.WorkShiftConverter;
import com.example.hotel_management_project.DTO.CustomException;
import com.example.hotel_management_project.DTO.Request.WorkShiftRequest;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.DTO.Response.WorkShiftResponse;
import com.example.hotel_management_project.Models.Entity.WorkShift;
import com.example.hotel_management_project.Models.Enums.ErrorCode;
import com.example.hotel_management_project.Repository.WorkShiftRepo;
import com.example.hotel_management_project.Services.WorkShiftService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkShiftServiceImpl implements WorkShiftService {
    private WorkShiftRepo workShiftRepo;
    private WorkShiftConverter workShiftConverter;

    public WorkShiftServiceImpl(WorkShiftConverter workShiftConverter, WorkShiftRepo workShiftRepo) {
        this.workShiftConverter = workShiftConverter;
        this.workShiftRepo = workShiftRepo;
    }

    @Override
    public PageResponse<WorkShiftResponse> getAllWorkShifts(Pageable pageable) {
        Page<WorkShift> page = workShiftRepo.findByActiveTrue(pageable);
        List<WorkShiftResponse> content = page.getContent().stream()
                .map(workShiftConverter::toWorkShiftResponse)
                .collect(Collectors.toList());

        return PageResponse.<WorkShiftResponse>builder()
                .pageNo(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .content(content)
                .build();
    }

    @Override
    public WorkShiftResponse createWorkShift(WorkShiftRequest workShiftRequest) {
        // Implementation for creating a new work shift
        checkWorkShift(workShiftRequest);
        WorkShift workShift = workShiftConverter.toWorkShiftEntity(workShiftRequest);
        if (workShift.getActive() == null) {
            workShift.setActive(true);
        }
        WorkShift savedWorkShift = workShiftRepo.save(workShift);
        return workShiftConverter.toWorkShiftResponse(savedWorkShift);
    }
    @Override
    public WorkShiftResponse updateWorkShift(Long id, WorkShiftRequest workShiftRequest) {
        // Implementation for updating an existing work shift
        WorkShift existingWorkShift = workShiftRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.WORK_SHIFT_NOT_FOUND));
        checkWorkShift(workShiftRequest);
        existingWorkShift.setShiftName(workShiftRequest.getName());
        existingWorkShift.setStartTime(workShiftRequest.getStartTime());
        existingWorkShift.setEndTime(workShiftRequest.getEndTime());

        WorkShift workShift= workShiftRepo.save(existingWorkShift);
        WorkShiftResponse workShiftResponse= workShiftConverter.toWorkShiftResponse(workShift);
        return workShiftResponse;
    }
    @Override
    public void deleteWorkShiftById(Long id) {
        WorkShift workShift = workShiftRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.WORK_SHIFT_NOT_FOUND));
        workShift.setActive(false);
        workShiftRepo.save(workShift);
    }
    public void checkWorkShift(WorkShiftRequest workShiftRequest) {

        if (workShiftRepo.existsByShiftName(workShiftRequest.getName())) {
            throw new CustomException(ErrorCode.WORK_SHIFT_EXISTED);
        }

        if (!workShiftRequest.getStartTime().isBefore(workShiftRequest.getEndTime())) {
            throw new CustomException(ErrorCode.INVALID_WORK_SHIFT_TIME);
        }

        if (workShiftRepo.existsByStartTimeAndEndTime(
                workShiftRequest.getStartTime(),
                workShiftRequest.getEndTime())) {
            throw new CustomException(ErrorCode.WORK_SHIFT_EXISTED);
        }

    }
}
