package com.bookingsystem.dto;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String email;

    // Constructors
    public JwtResponse() {}

    public JwtResponse(String token, String type, String username, String email) {
        this.token = token;
        this.type = type;
        this.username = username;
        this.email = email;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}