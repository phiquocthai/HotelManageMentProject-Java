package com.example.hotel_management_project.Controller;

import com.example.hotel_management_project.DTO.Request.CustomerRequest;
import com.example.hotel_management_project.DTO.Response.ApiResponse;
import com.example.hotel_management_project.DTO.Response.CustomerResponse;
import com.example.hotel_management_project.DTO.Response.PageResponse;
import com.example.hotel_management_project.Services.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createCustomer(@RequestBody @Valid CustomerRequest request) {
        CustomerResponse response = customerService.createCustomer(request);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Thêm khách hàng thành công");
        apiResponse.setResult(response);
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping(value = {"/{id}", "/search/{id}"})
    public ResponseEntity<ApiResponse> updateCustomer(@PathVariable Long id, @RequestBody @Valid CustomerRequest request) {
        CustomerResponse response = customerService.updateCustomer(id, request);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Cập nhật khách hàng thành công");
        apiResponse.setResult(response);
        return ResponseEntity.ok(apiResponse);
    }
    //@ModelAttribute: gom các tham số trên URL vào một object.
    @GetMapping("/search")
    public PageResponse<CustomerResponse> getCustomer(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                      @RequestParam(required = false, defaultValue = "10") int pageSize,
                                                      @RequestParam(required = false, defaultValue = "id") String sortBy,
                                                      @RequestParam(required = false, defaultValue = "asc") String sortDir,

                                                      @ModelAttribute CustomerRequest request)
    {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(Math.max(0, pageNo - 1), Math.max(1, pageSize), sort);

        PageResponse<CustomerResponse> customerPage = customerService.getCustomer(pageable,request);
        return customerPage;

    }

    @GetMapping
    public PageResponse<CustomerResponse> getAllCustomers(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                          @RequestParam(required = false, defaultValue = "10") int pageSize,
                                                          @RequestParam(required = false, defaultValue = "id") String sortBy,
                                                          @RequestParam(required = false, defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(Math.max(0, pageNo - 1), Math.max(1, pageSize), sort);
        return customerService.getAllCustomers(pageable);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Xóa khách hàng thành công");
        return ResponseEntity.ok(apiResponse);
    }
}
