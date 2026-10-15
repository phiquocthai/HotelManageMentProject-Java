package com.example.hotel_management_project.Services.Implements;

import com.example.hotel_management_project.Converter.RoomTypeConverter;
import com.example.hotel_management_project.DTO.Request.RoomTypeRequest;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.DTO.Response.RoomTypeResponse;
import com.example.hotel_management_project.Models.Entity.RoomType;
import com.example.hotel_management_project.Repository.RoomTypeRepo;
import com.example.hotel_management_project.Services.RoomTypeService;
import com.example.hotel_management_project.DTO.CustomException;
import com.example.hotel_management_project.Models.Enums.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeRepo roomTypeRepo;
    private final RoomTypeConverter roomTypeConverter;

    public RoomTypeServiceImpl(RoomTypeRepo roomTypeRepo, RoomTypeConverter roomTypeConverter) {
        this.roomTypeRepo = roomTypeRepo;
        this.roomTypeConverter = roomTypeConverter;
    }

    @Override
    public RoomTypeResponse createRoomType(RoomTypeRequest request) {
        if (roomTypeRepo.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.ROOM_TYPE_EXISTED); 
        }

        RoomType roomType = roomTypeConverter.toRoomTypeEntity(request);
        if (roomType.getActive() == null) {
            roomType.setActive(true);
        }
        roomType = roomTypeRepo.save(roomType);
        return roomTypeConverter.toRoomTypeResponse(roomType);
    }

    @Override
    public RoomTypeResponse updateRoomType(Long id, RoomTypeRequest request) {
        RoomType existingRoomType = roomTypeRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        if (!existingRoomType.getName().equals(request.getName()) && roomTypeRepo.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.ROOM_TYPE_EXISTED);
        }

        roomTypeConverter.updateEntity(request, existingRoomType);
        existingRoomType = roomTypeRepo.save(existingRoomType);
        return roomTypeConverter.toRoomTypeResponse(existingRoomType);
    }

    @Override
    public RoomTypeResponse getRoomTypeById(Long id) {
        RoomType roomType = roomTypeRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_TYPE_NOT_FOUND));
        return roomTypeConverter.toRoomTypeResponse(roomType);
    }

    @Override
    public List<RoomTypeResponse> getAllRoomTypes() {
        return roomTypeRepo.findByActiveTrue().stream()
                .map(roomTypeConverter::toRoomTypeResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<RoomTypeResponse> getAllRoomTypes(Pageable pageable) {
        Page<RoomType> page = roomTypeRepo.findByActiveTrue(pageable);
        List<RoomTypeResponse> content = page.getContent().stream()
                .map(roomTypeConverter::toRoomTypeResponse)
                .collect(Collectors.toList());

        return PageResponse.<RoomTypeResponse>builder()
                .pageNo(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .content(content)
                .build();
    }

    @Override
    public void deleteRoomType(Long id) {
        RoomType roomType = roomTypeRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_TYPE_NOT_FOUND));
        roomType.setActive(false);
        roomTypeRepo.save(roomType);
    }
}
