package com.example.hotel_management_project.Converter;

import com.example.hotel_management_project.DTO.Request.ServiceRequest;
import com.example.hotel_management_project.DTO.Response.ServiceResponse;
import com.example.hotel_management_project.Models.Entity.Service;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ServicesMapper {
    private final ModelMapper modelMapper;

    public ServicesMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Service toServiceEntity(ServiceRequest serviceRequest) {
        Service serviceEntity = modelMapper.map(serviceRequest, Service.class);
        if (serviceRequest.getActive() != null) {
            serviceEntity.setActive(serviceRequest.getActive());
        } else {
            serviceEntity.setActive(true);
        }
        return serviceEntity;
    }

    public ServiceResponse toServiceResponse(Service serviceEntity) {
        ServiceResponse serviceResponse = modelMapper.map(serviceEntity, ServiceResponse.class);
        if (serviceEntity.getCreatedBy() != null) {
            serviceResponse.setCreatedBy(serviceEntity.getCreatedBy().getFullName());
        }
        if (serviceEntity.getModifiedBy() != null) {
            serviceResponse.setModifiedBy(serviceEntity.getModifiedBy().getFullName());
        }
        return serviceResponse;
    }

    public void updateEntity(ServiceRequest request, Service entity) {
        entity.setName(request.getName());
        entity.setPrice(request.getPrice());
        entity.setImagePath(request.getImagePath());
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
    }
}
