package me.timur.findguide.reflection;

import me.timur.findguide.dto.GuideDto;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Temurbek Ismoilov on 10/05/23.
 */

public class Test {
    public static void main(String[] args){
        for(Field obj: GuideDto.class.getFields()) {
            System.out.println(obj.getName());
        }
    }
}
