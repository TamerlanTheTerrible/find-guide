package me.timur.findguide.dto;

import lombok.Data;

/**
 * Created by Temurbek Ismoilov on 30/04/23.
 */

@Data
public class GuideSearchParamDto {
    private String language;
    private String region;
    private String from;
    private String to;
    private Integer experience;
    private Boolean withCar;
    private Integer rating;
}
