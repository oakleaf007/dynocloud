package com.dyno.deaddrop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DeadDropPageController {

    @GetMapping({"/dead-drop", "/dead-drop/"})
    public String openDeadDropPage() {
        return "redirect:/dead-drop/index.html";
    }
}