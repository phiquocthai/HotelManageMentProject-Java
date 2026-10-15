package com.example.hotel_management_project.Security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthEntryPoint implements AuthenticationEntryPoint {
    //tạo đối tượng logger nhằm ghi log (debug, info, warn, error) trong class
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        logger.error("Unauthorized error: {}", authException.getMessage());
        System.out.println(authException);
//        HTTP/1.1 200 OK
//        Content-Type: application/json
//
//        {
//            "message": "Login successfully",
//                "token": "abcxyz"
//        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//Đặt HTTP status:401 Unauthorized

        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());
//        { "status": 401,
//                "error": "Unauthorized",
//                "message": "Full authentication is required to access this resource",
//                "path": "/api/admin/users" }
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
//        ObjectMapper của Jackson dùng để:
//
//        Chuyển Java Map → JSON và ghi JSON vào HTTP response.
    }
}