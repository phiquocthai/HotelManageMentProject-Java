package com.example.hotel_management_project.Security;

import com.example.hotel_management_project.Models.Entity.Staff;
import com.example.hotel_management_project.Repository.AuthRepo;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AuthRepo authRepo;

    public UserDetailsServiceImpl(AuthRepo authRepo) {
        this.authRepo = authRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Staff staff = authRepo.findByUsernameOrEmailOrPhoneNumber(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với tài khoản: " + username));

        List<SimpleGrantedAuthority> authorities = staff.getRole() != null
                ? List.of(new SimpleGrantedAuthority("ROLE_" + staff.getRole().name()))
                : Collections.emptyList();

        return new User(
                staff.getUsername(),
                staff.getPassword(),
                staff.getActive() != null ? staff.getActive() : true,
                true,
                true,
                true,
                authorities
        );
    }
}
