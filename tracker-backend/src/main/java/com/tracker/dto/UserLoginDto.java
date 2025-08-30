package com.tracker.dto;

import jakarta.validation.constraints.NotBlank;

public class UserLoginDto {
    
    @NotBlank(message = "Email or Employee ID is required")
    private String identifier; // Can be email or empId
    
    @NotBlank(message = "Password is required")
    private String password;
    
    // Default constructor
    public UserLoginDto() {}
    
    // Constructor with parameters
    public UserLoginDto(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }
    
    // Getters and Setters
    public String getIdentifier() {
        return identifier;
    }
    
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}