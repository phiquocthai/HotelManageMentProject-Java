package com.example.hotel_management_project.Controller;

import com.example.hotel_management_project.DTO.Request.PromotionRequest;
import com.example.hotel_management_project.DTO.Response.ApiResponse;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.DTO.Response.PromotionResponse;
import com.example.hotel_management_project.Services.PromotionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createPromotion(@RequestBody @Valid PromotionRequest request) {
        PromotionResponse response = promotionService.createPromotion(request);
        ApiResponse apiResponse = new ApiResponse();
        Map<String, Object> result = new HashMap<>();
        result.put("promotion", response);

        apiResponse.setMessage("Thêm khuyến mãi thành công");
        apiResponse.setResult(result);
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updatePromotion(@PathVariable Long id, @RequestBody @Valid PromotionRequest request) {
        PromotionResponse response = promotionService.updatePromotion(id, request);
        ApiResponse apiResponse = new ApiResponse();
        Map<String, Object> result = new HashMap<>();
        result.put("promotion", response);

        apiResponse.setMessage("Cập nhật khuyến mãi thành công");
        apiResponse.setResult(result);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public PageResponse<PromotionResponse> getAllPromotions(
            @RequestParam(required = false, defaultValue = "1") int pageNo,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(Math.max(0, pageNo - 1), Math.max(1, pageSize), sort);
        return promotionService.getAllPromotions(pageable);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Xóa khuyến mãi thành công");
        return ResponseEntity.ok(apiResponse);
    }
}
