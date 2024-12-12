package com.cloud6.place.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestController {

    @GetMapping("/test")
    public ModelAndView getAllPlaces() {
        ModelAndView mav = new ModelAndView("hello");  
        mav.addObject("message", "Hello"); 
        return mav;
    }
}

