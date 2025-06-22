package com.billiards_club_tuan_hung.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.billiards_club_tuan_hung.entity.EnumRole;
import com.billiards_club_tuan_hung.entity.Role;
import com.billiards_club_tuan_hung.entity.User;
import com.billiards_club_tuan_hung.payload.request.LoginRequest;
import com.billiards_club_tuan_hung.payload.request.SignupRequest;
import com.billiards_club_tuan_hung.payload.request.UpdateUserRoleRequest;
import com.billiards_club_tuan_hung.payload.response.JwtResponse;
import com.billiards_club_tuan_hung.payload.response.MessageResponse;
import com.billiards_club_tuan_hung.repository.RoleRepository;
import com.billiards_club_tuan_hung.repository.UserRepository;
import com.billiards_club_tuan_hung.security.jwt.JwtUtils;
import com.billiards_club_tuan_hung.security.service.UserDetailsImpl;

@CrossOrigin(origins = "http://127.0.0.1:5500", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Tên người dùng đã được sử dụng!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Email đã được sử dụng!"));
        }

        // Tạo tài khoản mới
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role defaultRole = roleRepository.findByName(EnumRole.ROLE_STAFF)
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy vai trò."));
            roles.add(defaultRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toLowerCase()) {
                    case "admin":
                    case "role_admin":
                        roles.add(roleRepository.findByName(EnumRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy vai trò ADMIN.")));
                        break;
                    case "staff":
                    case "role_staff":
                    default:
                        roles.add(roleRepository.findByName(EnumRole.ROLE_STAFF)
                                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy vai trò STAFF.")));
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Đăng ký thành công!"));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok().body(user))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update-role")
    public ResponseEntity<?> updateUserRole(@RequestBody UpdateUserRoleRequest request) {
        return userRepository.findById(request.getUserId())
                .map(user -> {
                    Set<Role> newRoles = new HashSet<>();

                    for (String roleStr : request.getRoles()) {
                        switch (roleStr.toLowerCase()) {
                            case "admin":
                            case "role_admin":
                                newRoles.add(roleRepository.findByName(EnumRole.ROLE_ADMIN)
                                        .orElseThrow(() -> new RuntimeException("Role ADMIN not found")));
                                break;
                            case "staff":
                            case "role_staff":
                            default:
                                newRoles.add(roleRepository.findByName(EnumRole.ROLE_STAFF)
                                        .orElseThrow(() -> new RuntimeException("Role STAFF not found")));
                                break;
                        }
                    }

                    user.setRoles(newRoles);
                    userRepository.save(user);
                    return ResponseEntity.ok(new MessageResponse("Cập nhật vai trò thành công!"));
                })
                .orElse(ResponseEntity.badRequest().body(new MessageResponse("Không tìm thấy người dùng")));
    }

    // Thêm endpoint này để frontend gọi lấy thông tin từ token
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(new MessageResponse("Token hết hạn hoặc không hợp lệ"));
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new JwtResponse(null, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }
}
