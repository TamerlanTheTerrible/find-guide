package me.timur.findguide.dto;

import lombok.Data;
import me.timur.findguide.constant.Language;

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
    private Language language;
    private String region;
    private Integer startYear;
    private String startMonth;
    private Integer startDate;
    private Integer endYear;
    private String endMonth;
    private Integer endDate;

    public UserProgress() {
        selectingLanguage = true;
    }

}