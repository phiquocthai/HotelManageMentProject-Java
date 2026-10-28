package com.example.hotel_management_project.Converter;

import com.example.hotel_management_project.DTO.Request.RoomRequest;
import com.example.hotel_management_project.DTO.Response.RoomResponse;
import com.example.hotel_management_project.Models.Entity.Room;
import com.example.hotel_management_project.Models.Entity.RoomType;
import com.example.hotel_management_project.Models.Enums.RoomStatus;
import org.springframework.stereotype.Component;

@Component
public class RoomConverter {

    public Room toRoomEntity(RoomRequest request, RoomType roomType) {
        RoomStatus status = RoomStatus.fromLabel(request.getStatus());
        return Room.builder()
                .roomNumber(request.getRoomNumber())
                .roomType(roomType)
                .floor(request.getFloor())
                .status(status)
                .note(request.getNote())
                .active(true)
                .build();
    }

    public RoomResponse toRoomResponse(Room entity) {
        if (entity == null) return null;
        RoomResponse response = new RoomResponse();
        response.setId(entity.getId());
        response.setRoomNumber(entity.getRoomNumber());
        response.setFloor(entity.getFloor());
        response.setStatus(entity.getStatus() != null ? entity.getStatus().getLabel() : null);
        if (entity.getRoomType() != null) {
            response.setRoomTypeName(entity.getRoomType().getName());
            response.setImagePath(entity.getRoomType().getImagePath());
        }
        response.setNote(entity.getNote());
        response.setCreatedBy(entity.getCreatedBy());
        response.setCreatedAt(entity.getCreatedAt());
        response.setModifiedBy(entity.getModifiedBy());
        response.setModifiedAt(entity.getModifiedAt());
        response.setActive(entity.getActive());
        return response;
    }

    public void updateEntity(RoomRequest request, Room entity, RoomType roomType) {
        entity.setRoomNumber(request.getRoomNumber());
        entity.setRoomType(roomType);
        entity.setFloor(request.getFloor());
        if (request.getStatus() != null) {
            entity.setStatus(RoomStatus.fromLabel(request.getStatus()));
        }
        entity.setNote(request.getNote());
    }
}
