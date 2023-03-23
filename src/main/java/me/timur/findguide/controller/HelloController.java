package me.timur.findguide.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * Created by Temurbek Ismoilov on 23/03/23.
 */

@Controller
public class HelloController {

    @GetMapping(value ="/hello")
    public String hello(Model model) {
        return "hello";
    }

    @PostMapping("/save")
    public String submit(@ModelAttribute Map<String, String> myEntity) {
        System.out.println(myEntity);
        return "redirect:/hello";
    }
}
