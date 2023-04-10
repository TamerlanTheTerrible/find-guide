package me.timur.findguide.exception;

import java.util.List;

/**
 * Created by Temurbek Ismoilov on 10/04/23.
 */

public class FindGuideBotException extends RuntimeException {
    private String message;

    public FindGuideBotException(String message) {
        this.message = message;
    }

    public FindGuideBotException(String message, Object... vars) {
        this.message = String.format(message, vars);
    }
}
