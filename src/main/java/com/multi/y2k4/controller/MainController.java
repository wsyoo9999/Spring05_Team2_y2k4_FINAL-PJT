package com.multi.y2k4.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping({"/"})
    public String index() {
        return "main";
    }
}
