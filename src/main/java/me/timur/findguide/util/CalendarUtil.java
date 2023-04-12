package me.timur.findguide.util;

import me.timur.findguide.exception.FindGuideBotException;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

/**
 * Created by Temurbek Ismoilov on 10/04/23.
 */

public class CalendarUtil {

    private static final Map<String, Integer> monthMap;

    static {
        monthMap = new LinkedHashMap<>();
        var now = LocalDate.now().withMonth(1);
        for (int i = 1; i <= 12; i++) {
            monthMap.put(now.getMonth().name(), i);
            now = now.plusMonths(1);
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
         List<String> monthNames = new ArrayList<>(monthMap.keySet());
        if (year != now.getYear()) {
            return monthNames;
        } else {
           int currentMonth = now.getMonth().getValue();
           return monthNames.subList(currentMonth-1, monthMap.size());
        }
    }

    public static List<Integer> daysOf(int year, @NonNull String month) {
        final LocalDate now = LocalDate.now();
        if (year < now.getYear()) {
            throw new FindGuideBotException("Invalid value of year %s", year);
        }

        if (monthMap.get(month) == null) {
            throw new FindGuideBotException("Invalid value of month %s", month);
        }

        int monthNumber = monthMap.get(month);
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

    public static Integer monthNumber(String monthName) {
        final Integer monthNumber = monthMap.get(monthName);
        if (monthNumber == null) {
            throw new FindGuideBotException("Invalid month name value: %s", monthName);
        }
        return monthNumber;
    }
}
