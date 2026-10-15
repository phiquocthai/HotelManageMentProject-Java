package com.example.hotel_management_project.Services;

import com.example.hotel_management_project.DTO.Request.RoomRequest;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.DTO.Response.RoomResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoomService {
    RoomResponse createRoom(RoomRequest request);
    RoomResponse updateRoom(Long id, RoomRequest request);
    RoomResponse getRoomById(Long id);

    PageResponse<RoomResponse> getAllRooms(Pageable pageable);
    void deleteRoom(Long id);
}
