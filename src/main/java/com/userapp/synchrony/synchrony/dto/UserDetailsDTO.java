package com.userapp.synchrony.synchrony.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDTO {
    private String userName;
    private String password;
    private String userEmail;
    private String dob;
    private List<String> imagesList;
}
