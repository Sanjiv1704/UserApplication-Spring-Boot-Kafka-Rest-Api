package com.userapp.synchrony.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDTO {
    private String userName;
    private String password;
    private String userEmail;
    private String designation;
}
