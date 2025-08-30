package com.tracker.service;

import com.tracker.dto.AuthResponse;
import com.tracker.dto.UserLoginDto;
import com.tracker.dto.UserRegistrationDto;
import com.tracker.entity.User;
import com.tracker.repository.UserRepository;
import com.tracker.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Register a new user
     */
    public AuthResponse registerUser(UserRegistrationDto registrationDto) {
        try {
            // Validate password confirmation
            if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
                return new AuthResponse("Password and confirm password do not match");
            }
            
            // Check if email already exists
            if (userRepository.existsByCompanyEmail(registrationDto.getCompanyEmail())) {
                return new AuthResponse("Email already exists");
            }
            
            // Check if employee ID already exists
            if (userRepository.existsByEmpId(registrationDto.getEmpId())) {
                return new AuthResponse("Employee ID already exists");
            }
            
            // Create new user
            User user = new User();
            user.setFullName(registrationDto.getFullName());
            user.setDepartment(registrationDto.getDepartment());
            user.setEmpId(registrationDto.getEmpId());
            user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
            user.setMobileNo(registrationDto.getMobileNo());
            user.setCompanyEmail(registrationDto.getCompanyEmail());
            
            // Save user to database
            User savedUser = userRepository.save(user);
            
            // Generate JWT token
            String token = jwtUtil.generateToken(
                savedUser.getCompanyEmail(),
                savedUser.getId(),
                savedUser.getFullName()
            );
            
            return new AuthResponse(token, savedUser.getId(), savedUser.getFullName(), savedUser.getEmpId(), savedUser.getCompanyEmail());
            
        } catch (Exception e) {
            return new AuthResponse("Registration failed: " + e.getMessage());
        }
    }
    
    /**
     * Authenticate user login
     */
    public AuthResponse loginUser(UserLoginDto loginDto) {
        try {
            // Find user by email or employee ID
            Optional<User> userOptional = userRepository.findByEmailOrEmpId(loginDto.getIdentifier());
            
            if (userOptional.isEmpty()) {
                return new AuthResponse("Invalid credentials");
            }
            
            User user = userOptional.get();
            
            // Verify password
            if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                return new AuthResponse("Invalid credentials");
            }
            
            // Generate JWT token
            String token = jwtUtil.generateToken(
                user.getCompanyEmail(),
                user.getId(),
                user.getFullName()
            );
            
            return new AuthResponse(token, user.getId(), user.getFullName(), user.getCompanyEmail());
            
        } catch (Exception e) {
            return new AuthResponse("Login failed: " + e.getMessage());
        }
    }
    
    /**
     * Get user profile by ID
     */
    public User getUserProfile(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
    
    /**
     * Check if user exists by email
     */
    public boolean userExistsByEmail(String email) {
        return userRepository.existsByCompanyEmail(email);
    }
    
    /**
     * Find user by mobile number (for password reset)
     */
    public User findByMobileNo(String mobileNo) {
        return userRepository.findByMobileNo(mobileNo).orElse(null);
    }
}