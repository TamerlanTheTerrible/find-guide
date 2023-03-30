package me.timur.findguide.dto;

import lombok.Data;

/**
 * Created by Temurbek Ismoilov on 28/03/23.
 */

@Data
public class UserProgress {
    private boolean selectingLanguage;
    private boolean selectingRegion;
    private boolean selectingStartYear;
    private boolean selectingStartMonth;
    private boolean selectingStartDate;
    private boolean selectingEndYear;
    private boolean selectingEndMonth;
    private boolean selectingEndDate;
    private String language;
    private String region;
    private String startYear;
    private String startMonth;
    private String startDate;
    private String endYear;
    private String endMonth;
    private String endDate;

    public UserProgress() {
        selectingLanguage = true;
    }

}