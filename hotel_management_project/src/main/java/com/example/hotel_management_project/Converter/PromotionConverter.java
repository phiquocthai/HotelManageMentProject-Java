package com.example.hotel_management_project.Converter;

import com.example.hotel_management_project.DTO.Request.PromotionRequest;
import com.example.hotel_management_project.DTO.Response.PromotionResponse;
import com.example.hotel_management_project.Models.Entity.Promotion;
import com.example.hotel_management_project.Models.Enums.DiscountType;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PromotionConverter {

    private final ModelMapper modelMapper;

    public PromotionConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Promotion toPromotionEntity(PromotionRequest request) {
        Promotion promotion = Promotion.builder()
                .code(request.getCode())
                .name(request.getName())
                .discountType(parseDiscountType(request.getDiscountType()))
                .discountValue(request.getDiscountValue())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
        return promotion;
    }

    public PromotionResponse toPromotionResponse(Promotion promotion) {
        PromotionResponse response = PromotionResponse.builder()
                .id(promotion.getId())
                .code(promotion.getCode())
                .name(promotion.getName())
                .discountType(promotion.getDiscountType())
                .discountValue(promotion.getDiscountValue())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .active(promotion.getActive())
                .createdAt(promotion.getCreatedAt())
                .modifiedAt(promotion.getModifiedAt())
                .build();

        if (promotion.getCreatedBy() != null) {
            response.setCreatedBy(promotion.getCreatedBy().getFullName());
        }
        if (promotion.getModifiedBy() != null) {
            response.setModifiedBy(promotion.getModifiedBy().getFullName());
        }

        return response;
    }

    public void updateEntity(PromotionRequest request, Promotion entity) {
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        if (request.getDiscountType() != null && !request.getDiscountType().trim().isEmpty()) {
            entity.setDiscountType(parseDiscountType(request.getDiscountType()));
        }
        entity.setDiscountValue(request.getDiscountValue());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
    }

    public DiscountType parseDiscountType(String typeStr) {
        if (typeStr == null || typeStr.trim().isEmpty()) {
            return null;
        }
        String trimmed = typeStr.trim();
        for (DiscountType dt : DiscountType.values()) {
            if (dt.name().equalsIgnoreCase(trimmed) || dt.getLabel().equalsIgnoreCase(trimmed)) {
                return dt;
            }
        }
        throw new IllegalArgumentException("Loại giảm giá không hợp lệ: " + typeStr);
    }
}
