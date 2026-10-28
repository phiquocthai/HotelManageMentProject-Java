package com.example.hotel_management_project.Services.Implements;

import com.example.hotel_management_project.Converter.RoomConverter;
import com.example.hotel_management_project.DTO.CustomException;
import com.example.hotel_management_project.DTO.Request.RoomRequest;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.DTO.Response.RoomResponse;
import com.example.hotel_management_project.Models.Entity.Room;
import com.example.hotel_management_project.Models.Entity.RoomType;
import com.example.hotel_management_project.Models.Enums.ErrorCode;
import com.example.hotel_management_project.Repository.RoomRepo;
import com.example.hotel_management_project.Repository.RoomTypeRepo;
import com.example.hotel_management_project.Services.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepo roomRepo;
    private final RoomTypeRepo roomTypeRepo;
    private final RoomConverter roomConverter;

    /**
     * Thêm mới phòng
     * 1. Kiểm tra xem số phòng đã tồn tại trong hệ thống chưa.
     * 2. Tìm loại phòng theo roomTypeId.
     * 3. Chuyển đổi DTO sang Entity và lưu vào database.
     * 4. Trả về RoomResponse.
     */
    @Override
    public RoomResponse createRoom(RoomRequest request) {
        if (roomRepo.existsByRoomNumber(request.getRoomNumber())) {
            throw new CustomException(ErrorCode.ROOM_EXISTED);
        }

        RoomType roomType = roomTypeRepo.findById(request.getRoomTypeId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        Room room = roomConverter.toRoomEntity(request, roomType);
        Room savedRoom = roomRepo.save(room);
        return roomConverter.toRoomResponse(savedRoom);
    }

    /**
     * Cập nhật thông tin phòng
     * 1. Kiểm tra sự tồn tại của phòng cần sửa.
     * 2. Nếu đổi số phòng, kiểm tra số phòng mới có bị trùng không.
     * 3. Tìm loại phòng mới theo roomTypeId.
     * 4. Cập nhật dữ liệu Entity và lưu vào database.
     */
    @Override
    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room room = roomRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        if (!room.getRoomNumber().equalsIgnoreCase(request.getRoomNumber())
                && roomRepo.existsByRoomNumber(request.getRoomNumber())) {
            throw new CustomException(ErrorCode.ROOM_EXISTED);
        }

        RoomType roomType = roomTypeRepo.findById(request.getRoomTypeId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        roomConverter.updateEntity(request, room, roomType);
        Room updatedRoom = roomRepo.save(room);
        return roomConverter.toRoomResponse(updatedRoom);
    }

    /**
     * Lấy thông tin chi tiết một phòng theo ID
     */
    @Override
    public RoomResponse getRoomById(Long id) {
        Room room = roomRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        return roomConverter.toRoomResponse(room);
    }

    /**
     * Lấy danh sách phòng đang hoạt động (active = true) có phân trang
     */
    @Override
    public PageResponse<RoomResponse> getAllRooms(Pageable pageable) {
        Page<Room> page = roomRepo.findByActiveTrue(pageable);
        List<RoomResponse> content = page.getContent().stream()
                .map(roomConverter::toRoomResponse)
                .collect(Collectors.toList());

        return PageResponse.<RoomResponse>builder()
                .pageNo(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .content(content)
                .build();
    }

    /**
     * Xóa mềm phòng (active = false)
     */
    @Override
    public void deleteRoom(Long id) {
        Room room = roomRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        room.setActive(false);
        roomRepo.save(room);
    }
}
