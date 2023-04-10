package me.timur.findguide.util;

import me.timur.findguide.exception.FindGuideBotException;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Temurbek Ismoilov on 10/04/23.
 */

public class CalendarUtil {

    private static final ArrayList<String> MONTHS;

    static {
        MONTHS = new ArrayList<>();
        var now = LocalDate.now().withMonth(1);
        for (int i = 0; i < 12; i++) {
            MONTHS.add(now.getMonth().name());
        }
    }

    public static List<Integer> years(int numberOfYears) {
        List<Integer> years = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();
        years.add(currentYear);

        for (int i = 1; i < numberOfYears; i++) {
          years.add(++currentYear);
        }

        return years;
    }

    public static List<String> monthsOf(int year) {
        final LocalDate now = LocalDate.now();
        if (year < now.getYear()) {
            throw new FindGuideBotException("Invalid value of year %s", year);
        }

        if (year != now.getYear()) {
            return MONTHS;
        } else {
           int currentMonth = now.getMonth().getValue();
           return MONTHS.subList(currentMonth-1, MONTHS.size());
        }
    }

    public static List<Integer> daysOf(int year, @NonNull String month) {
        final LocalDate now = LocalDate.now();
        if (year < now.getYear()) {
            throw new FindGuideBotException("Invalid value of year %s", year);
        }

        if (!MONTHS.contains(month)) {
            throw new FindGuideBotException("Invalid value of month %s", month);
        }

        int monthNumber = MONTHS.indexOf(month)+1;
        int lastDay = YearMonth.now().withMonth(monthNumber).atEndOfMonth().getDayOfMonth();
        int firstDay = 1;
        if (now.getYear() == year && now.getMonth().name().equals(month)) {
            firstDay = now.getDayOfMonth();
        }

        List<Integer> days = new ArrayList<>();
        for (int i = firstDay; i <= lastDay; i++) {
            days.add(i);
        }

        return days;
    }

}
