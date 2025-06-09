package com.expensetracker.api.dto;

public class UserResponseDTO {
    private String name;
    private String email;

    public UserResponseDTO(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters (no need for setters unless you want mutability)
    public String getName() { return name; }
    public String getEmail() { return email; }
}
