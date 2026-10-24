package com.example.hotel_management_project.Converter;

import com.example.hotel_management_project.DTO.Request.RoomRequest;
import com.example.hotel_management_project.DTO.Request.RoomTypeRequest;
import com.example.hotel_management_project.DTO.Response.RoomResponse;
import com.example.hotel_management_project.Models.Entity.Room;
import com.example.hotel_management_project.Models.Entity.RoomType;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class RoomConverter {
    private final ModelMapper modelMapper;

    public RoomConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Room toRoomEntity(RoomRequest roomRequest) {
        Room roomEntity = modelMapper.map(roomRequest, Room.class);
        return roomEntity;
    }

    public RoomResponse toRoomResponse(Room roomEntity) {
        RoomResponse roomResponse = modelMapper.map(roomEntity, RoomResponse.class);

        return roomResponse;
    }

    public void updateEntity(RoomTypeRequest request, RoomType entity) {
        entity.setName(request.getName());
        entity.setBasePrice(request.getBasePrice());
        entity.setMaxOccupancy(request.getMaxOccupancy());
        entity.setImagePath(request.getImagePath());
    }
}
