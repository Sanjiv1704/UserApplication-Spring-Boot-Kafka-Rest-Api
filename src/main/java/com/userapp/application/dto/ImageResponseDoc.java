package com.userapp.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponseDoc {
    private DataDTO data;
    private Boolean success;
    private Integer status;
}
