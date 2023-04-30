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
    private Integer startMonth;
    private Integer startDate;
    private Integer endYear;
    private Integer endMonth;
    private Integer endDate;

    public UserProgress() {
        selectingLanguage = true;
    }

    public void progress(String data) {

    }
}