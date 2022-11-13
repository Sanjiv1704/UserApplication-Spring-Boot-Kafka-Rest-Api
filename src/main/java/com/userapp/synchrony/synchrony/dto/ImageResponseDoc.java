package com.userapp.synchrony.synchrony.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponseDoc {
    private DataDTO dataDTO;
    private Boolean success;
    private Integer status;
}
