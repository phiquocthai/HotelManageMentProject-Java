package com.example.hotel_management_project.Converter;

import com.example.hotel_management_project.DTO.Request.WorkShiftRequest;
import com.example.hotel_management_project.DTO.Response.WorkShiftResponse;
import com.example.hotel_management_project.Models.Entity.WorkShift;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class WorkShiftConverter {
    private final ModelMapper modelMapper;

    public WorkShiftConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public WorkShift toWorkShiftEntity(WorkShiftRequest workShiftRequest) {
        WorkShift workShiftEntity = modelMapper.map(workShiftRequest, WorkShift.class);
        return workShiftEntity;
    }
    public WorkShiftResponse toWorkShiftResponse(WorkShift workShiftEntity) {
        WorkShiftResponse workShiftResponse = modelMapper.map(workShiftEntity, WorkShiftResponse.class);
        return workShiftResponse;
    }
}
