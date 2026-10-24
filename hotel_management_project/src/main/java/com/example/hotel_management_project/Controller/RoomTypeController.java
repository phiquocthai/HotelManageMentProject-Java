package com.example.hotel_management_project.Controller;

import com.example.hotel_management_project.DTO.Request.RoomTypeRequest;
import com.example.hotel_management_project.DTO.Response.ApiResponse;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.DTO.Response.RoomTypeResponse;
import com.example.hotel_management_project.Services.RoomTypeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/room-type")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    public RoomTypeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createRoomType(@RequestBody @Valid RoomTypeRequest request) {
        RoomTypeResponse response = roomTypeService.createRoomType(request);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Thêm loại phòng thành công");
        apiResponse.setResult(response);
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateRoomType(@PathVariable Long id, @RequestBody @Valid RoomTypeRequest request) {
        RoomTypeResponse response = roomTypeService.updateRoomType(id, request);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Cập nhật loại phòng thành công");
        apiResponse.setResult(response);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getRoomTypeById(@PathVariable Long id) {
        RoomTypeResponse response = roomTypeService.getRoomTypeById(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setResult(response);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public PageResponse<RoomTypeResponse> getAllRoomTypes(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                          @RequestParam(required = false, defaultValue = "10") int pageSize,
                                                          @RequestParam(required = false, defaultValue = "id") String sortBy,
                                                          @RequestParam(required = false, defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(Math.max(0, pageNo - 1), Math.max(1, pageSize), sort);
        return roomTypeService.getAllRoomTypes(pageable);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteRoomType(@PathVariable Long id) {
        roomTypeService.deleteRoomType(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Xóa loại phòng thành công");
        return ResponseEntity.ok(apiResponse);
    }
}
