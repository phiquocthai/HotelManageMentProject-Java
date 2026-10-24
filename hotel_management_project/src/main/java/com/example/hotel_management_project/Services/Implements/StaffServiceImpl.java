package com.example.hotel_management_project.Services.Implements;

import com.example.hotel_management_project.DTO.Request.StaffRequest;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.DTO.Response.StaffResponse;
import com.example.hotel_management_project.Models.Entity.Staff;
import com.example.hotel_management_project.Repository.StaffRepo;
import com.example.hotel_management_project.Services.StaffService;
import com.example.hotel_management_project.DTO.CustomException;
import com.example.hotel_management_project.Models.Enums.ErrorCode;
import com.example.hotel_management_project.Converter.StaffConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StaffServiceImpl implements StaffService {

    private final StaffRepo staffRepo;
    private final StaffConverter staffConverter;
    private final PasswordEncoder passwordEncoder;

    public StaffServiceImpl(StaffRepo staffRepo, StaffConverter staffConverter, PasswordEncoder passwordEncoder) {
        this.staffRepo = staffRepo;
        this.staffConverter = staffConverter;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public StaffResponse createStaff(StaffRequest request) {
        if (staffRepo.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.STAFF_EXISTED);
        }
        if (staffRepo.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.STAFF_EXISTED);
        }

        Staff staff = staffConverter.toStaffEntity(request);
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            staff.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getActive() == null) {
            staff.setActive(true);
        }

        staff = staffRepo.save(staff);
        return staffConverter.toStaffResponse(staff);
    }

    @Override
    public StaffResponse updateStaff(Long id, StaffRequest request) {
        Staff existingStaff = staffRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.STAFF_NOT_FOUND));

        if (!existingStaff.getUsername().equals(request.getUsername()) && staffRepo.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.STAFF_EXISTED);
        }
        if (!existingStaff.getEmail().equals(request.getEmail()) && staffRepo.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.STAFF_EXISTED);
        }

        staffConverter.updateEntity(request, existingStaff);

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingStaff.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        existingStaff = staffRepo.save(existingStaff);
        return staffConverter.toStaffResponse(existingStaff);
    }

    @Override
    public StaffResponse getStaffById(Long id) {
        Staff staff = staffRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.STAFF_NOT_FOUND));
        return staffConverter.toStaffResponse(staff);
    }

    @Override
    public PageResponse<StaffResponse> getAllStaffs(Pageable pageable) {
        Page<Staff> page = staffRepo.findByActiveTrue(pageable);
        List<StaffResponse> content = new ArrayList<>();
        for (Staff staff : page.getContent()) {
            content.add(staffConverter.toStaffResponse(staff));
        }

        return PageResponse.<StaffResponse>builder()
                .pageNo(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .content(content)
                .build();
    }

    @Override
    public void deleteStaff(Long id) {
        Staff staff = staffRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.STAFF_NOT_FOUND));
        staff.setActive(false);
        staffRepo.save(staff);
    }
}
