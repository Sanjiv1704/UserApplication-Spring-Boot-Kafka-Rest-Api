package com.userapp.synchrony.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataDTO {
    private String id;
    private String title;
    private String description;
    private Integer datetime;
    private String type;
    private Boolean animated;
    private Integer width;
    private Integer height;
    private Integer size;
    private Integer views;
    private Integer bandwidth;
    private String deletehash;
    private String name;
    private String section;
    private String link;
    private String gifv;
    private String mp4;
    private Boolean looping;
    private Boolean favorite;
    private Boolean nsfw;
    private String vote;
    private Boolean in_gallery;


}
