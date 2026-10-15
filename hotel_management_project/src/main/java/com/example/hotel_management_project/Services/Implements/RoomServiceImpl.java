package com.example.hotel_management_project.Services.Implements;

import com.example.hotel_management_project.Converter.RoomConverter;
import com.example.hotel_management_project.DTO.CustomException;
import com.example.hotel_management_project.DTO.Request.RoomRequest;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.DTO.Response.RoomResponse;
import com.example.hotel_management_project.Models.Entity.Room;
import com.example.hotel_management_project.Models.Enums.ErrorCode;
import com.example.hotel_management_project.Repository.RoomRepo;
import com.example.hotel_management_project.Services.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepo roomRepo;
    private final RoomConverter roomConverter;

    @Override
    public RoomResponse createRoom(RoomRequest request) {
        return null;
    }

    @Override
    public RoomResponse updateRoom(Long id, RoomRequest request) {
        return null;
    }

    @Override
    public RoomResponse getRoomById(Long id) {
        return null;
    }

    @Override
    public PageResponse<RoomResponse> getAllRooms(Pageable pageable) {
        Page<Room> page = roomRepo.findByActiveTrue(pageable);
        List<RoomResponse> content = page.getContent().stream()
                .map(roomConverter::toRoomResponse)
                .collect(Collectors.toList());

        return PageResponse.<RoomResponse>builder()
                .pageNo(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .content(content)
                .build();
    }

    @Override
    public void deleteRoom(Long id) {
        Room room = roomRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        room.setActive(false);
        roomRepo.save(room);
    }
}
