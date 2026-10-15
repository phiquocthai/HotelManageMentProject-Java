package com.example.hotel_management_project.Controller;

import com.example.hotel_management_project.DTO.Request.RoomRequest;
import com.example.hotel_management_project.DTO.Response.ApiResponse;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.DTO.Response.RoomResponse;
import com.example.hotel_management_project.Services.RoomService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createRoom(@RequestBody @Valid RoomRequest request) {
        RoomResponse response = roomService.createRoom(request);
        ApiResponse apiResponse = new ApiResponse();
        Map<String, Object> result = new HashMap<>();

        result.put("room", response);
        apiResponse.setMessage("Thêm phòng thành công");
        apiResponse.setResult(result);
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateRoom(@PathVariable Long id, @RequestBody @Valid RoomRequest request) {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getRoomById(@PathVariable Long id) {
        return null;
    }

    @GetMapping
    public PageResponse<RoomResponse> getAllRooms(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                  @RequestParam(required = false, defaultValue = "10") int pageSize,
                                                  @RequestParam(required = false, defaultValue = "id") String sortBy,
                                                  @RequestParam(required = false, defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(Math.max(0, pageNo - 1), Math.max(1, pageSize), sort);
        return roomService.getAllRooms(pageable);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Xóa phòng thành công");
        return ResponseEntity.ok(apiResponse);
    }
}
