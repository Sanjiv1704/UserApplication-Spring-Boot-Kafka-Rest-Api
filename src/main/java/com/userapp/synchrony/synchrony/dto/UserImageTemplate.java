package com.userapp.synchrony.synchrony.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserImageTemplate {
    private String userName;
    private String imageName;
    private String imageUrl;
    private String opType;
}
