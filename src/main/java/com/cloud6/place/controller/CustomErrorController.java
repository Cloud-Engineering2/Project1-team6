package com.cloud6.place.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error/401")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ModelAndView unauthorizedError() {
        return new ModelAndView("error-401");
    }
}

