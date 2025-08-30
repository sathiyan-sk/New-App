package com.tracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegistrationDto {
    
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;
    
    @NotBlank(message = "Department is required")
    @Size(min = 2, max = 50, message = "Department must be between 2 and 50 characters")
    private String department;
    
    @NotBlank(message = "Employee ID is required")
    @Size(min = 3, max = 20, message = "Employee ID must be between 3 and 20 characters")
    private String empId;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
    
    @NotBlank(message = "Mobile number is required")
    @Size(min = 10, max = 15, message = "Mobile number must be between 10 and 15 characters")
    private String mobileNo;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String companyEmail;
    
    // Default constructor
    public UserRegistrationDto() {}
    
    // Constructor with parameters
    public UserRegistrationDto(String fullName, String department, String empId, 
                              String password, String confirmPassword, String mobileNo, String companyEmail) {
        this.fullName = fullName;
        this.department = department;
        this.empId = empId;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.mobileNo = mobileNo;
        this.companyEmail = companyEmail;
    }
    
    // Getters and Setters
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getEmpId() {
        return empId;
    }
    
    public void setEmpId(String empId) {
        this.empId = empId;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    public String getMobileNo() {
        return mobileNo;
    }
    
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }
    
    public String getCompanyEmail() {
        return companyEmail;
    }
    
    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }
}