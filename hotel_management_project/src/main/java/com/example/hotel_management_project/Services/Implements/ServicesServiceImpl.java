package com.example.hotel_management_project.Services.Implements;

import com.example.hotel_management_project.Converter.ServicesMapper;
import com.example.hotel_management_project.DTO.CustomException;
import com.example.hotel_management_project.DTO.Request.ServiceRequest;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.DTO.Response.ServiceResponse;
import com.example.hotel_management_project.Models.Entity.Service;
import com.example.hotel_management_project.Models.Enums.ErrorCode;
import com.example.hotel_management_project.Repository.ServiceRepo;
import com.example.hotel_management_project.Services.ServicesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServicesServiceImpl implements ServicesService {

    private final ServiceRepo serviceRepo;
    private final ServicesMapper servicesMapper;

    public ServicesServiceImpl(ServiceRepo serviceRepo, ServicesMapper servicesMapper) {
        this.serviceRepo = serviceRepo;
        this.servicesMapper = servicesMapper;
    }

    @Override
    public PageResponse<ServiceResponse> getAllServices(Pageable pageable) {
        Page<Service> page = serviceRepo.findByActiveTrue(pageable);
        List<ServiceResponse> content = page.getContent().stream()
                .map(servicesMapper::toServiceResponse)
                .collect(Collectors.toList());

        return PageResponse.<ServiceResponse>builder()
                .pageNo(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .content(content)
                .build();
    }

    @Override
    public List<ServiceResponse> getAllServices() {
        return serviceRepo.findByActiveTrue().stream()
                .map(servicesMapper::toServiceResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ServiceResponse getServiceById(Long id) {
        Service service = serviceRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.SERVICE_NOT_FOUND));
        return servicesMapper.toServiceResponse(service);
    }

    @Override
    public ServiceResponse createService(ServiceRequest request) {
        if (serviceRepo.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.SERVICE_EXISTED);
        }

        Service service = servicesMapper.toServiceEntity(request);
        if (service.getActive() == null) {
            service.setActive(true);
        }
        service = serviceRepo.save(service);
        return servicesMapper.toServiceResponse(service);
    }

    @Override
    public ServiceResponse updateService(Long id, ServiceRequest request) {
        Service existingService = serviceRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.SERVICE_NOT_FOUND));

        if (!existingService.getName().equals(request.getName()) && serviceRepo.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.SERVICE_EXISTED);
        }

        servicesMapper.updateEntity(request, existingService);
        existingService = serviceRepo.save(existingService);
        return servicesMapper.toServiceResponse(existingService);
    }

    @Override
    public ServiceResponse deleteService(Long id) {
        Service service = serviceRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.SERVICE_NOT_FOUND));
        service.setActive(false);
        service = serviceRepo.save(service);
        return servicesMapper.toServiceResponse(service);
    }
}
