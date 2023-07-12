package com.sunlifemalaysia.app4share.controller.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sunlifemalaysia.app4share.exception.PasswordException;
import com.sunlifemalaysia.app4share.model.dto.PasswordDto;
import com.sunlifemalaysia.app4share.service.UserAccountService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserAccountService userAccountService;

    public ProfileController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping
    public String showProfilePage() {
        return "profile";
    }

    @PostMapping
    public String updateProfile(RedirectAttributes redirectAttributes, Authentication authentication,
            @Valid PasswordDto passwordDto) {

        if (authentication.getPrincipal() instanceof UserDetails userDetails) {

            try {
                userAccountService.changePassword(userDetails, passwordDto);
                redirectAttributes.addFlashAttribute("message", "Password updated.");
            } catch (PasswordException passwordException) {

                redirectAttributes.addFlashAttribute("error", passwordException.getReason());

            }

        }

        return "redirect:/profile";
    }

}
