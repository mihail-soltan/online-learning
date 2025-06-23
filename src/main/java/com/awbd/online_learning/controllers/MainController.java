package com.awbd.online_learning.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping({"/", "/main", "/index", "/home"})
    public String mainPage() {
        return "main";
    }
}