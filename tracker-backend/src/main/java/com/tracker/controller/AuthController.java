package com.tracker.controller;

import com.tracker.dto.*;
import com.tracker.entity.User;
import com.tracker.service.AuthService;
import com.tracker.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Tracker Pro Backend is running!", "OK"));
    }
    
    /**
     * User Registration Endpoint
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> registerUser(
            @Valid @RequestBody UserRegistrationDto registrationDto,
            BindingResult bindingResult) {
        
        // Validate input
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Validation failed: " + errors));
        }
        
        // Register user
        AuthResponse authResponse = authService.registerUser(registrationDto);
        
        if (authResponse.getToken() != null) {
            return ResponseEntity.ok(
                ApiResponse.success("User registered successfully!", authResponse)
            );
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(authResponse.getMessage()));
        }
    }
    
    /**
     * User Login Endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> loginUser(
            @Valid @RequestBody UserLoginDto loginDto,
            BindingResult bindingResult) {
        
        // Validate input
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Validation failed: " + errors));
        }
        
        // Authenticate user
        AuthResponse authResponse = authService.loginUser(loginDto);
        
        if (authResponse.getToken() != null) {
            return ResponseEntity.ok(
                ApiResponse.success("Login successful!", authResponse)
            );
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(authResponse.getMessage()));
        }
    }
    
    /**
     * Get Current User Profile
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserProfile(Authentication authentication) {
        try {
            String username = authentication.getName();
            // Extract user ID from token (you'll need to get it from the request header)
            // For now, let's find by email
            User user = authService.getUserProfile(1L); // This needs to be properly implemented
            
            if (user != null) {
                Map<String, Object> userProfile = new HashMap<>();
                userProfile.put("id", user.getId());
                userProfile.put("fullName", user.getFullName());
                userProfile.put("department", user.getDepartment());
                userProfile.put("empId", user.getEmpId());
                userProfile.put("mobileNo", user.getMobileNo());
                userProfile.put("companyEmail", user.getCompanyEmail());
                userProfile.put("createdAt", user.getCreatedAt());
                
                return ResponseEntity.ok(
                    ApiResponse.success("Profile retrieved successfully!", userProfile)
                );
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve profile: " + e.getMessage()));
        }
    }
    
    /**
     * Check if email exists
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailExists(@RequestParam String email) {
        boolean exists = authService.userExistsByEmail(email);
        return ResponseEntity.ok(
            ApiResponse.success("Email check completed", exists)
        );
    }
    
    /**
     * Password Reset Request (Find user by mobile/email)
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestParam String identifier) {
        try {
            // Try to find user by mobile number first, then by email
            User user = authService.findByMobileNo(identifier);
            if (user == null) {
                // If not found by mobile, try by email
                if (authService.userExistsByEmail(identifier)) {
                    return ResponseEntity.ok(
                        ApiResponse.success("Password reset instructions will be sent to your registered mobile number")
                    );
                }
            }
            
            if (user != null) {
                return ResponseEntity.ok(
                    ApiResponse.success("Password reset instructions will be sent to your registered mobile number")
                );
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("No account found with this mobile number or email"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to process password reset request"));
        }
    }
    
    /**
     * Validate JWT Token
     */
    @PostMapping("/validate-token")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                String username = jwtUtil.extractUsername(jwtToken);
                
                if (jwtUtil.validateToken(jwtToken, username)) {
                    Map<String, Object> tokenInfo = new HashMap<>();
                    tokenInfo.put("valid", true);
                    tokenInfo.put("username", username);
                    tokenInfo.put("userId", jwtUtil.extractUserId(jwtToken));
                    tokenInfo.put("fullName", jwtUtil.extractFullName(jwtToken));
                    
                    return ResponseEntity.ok(
                        ApiResponse.success("Token is valid", tokenInfo)
                    );
                }
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid token"));
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Token validation failed"));
        }
    }
}