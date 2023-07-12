package com.sunlifemalaysia.app4share.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sign-in")
public class SignInController {
    
    @GetMapping
    public String showSignInPage() {
        return "sign-in";
    }
}
