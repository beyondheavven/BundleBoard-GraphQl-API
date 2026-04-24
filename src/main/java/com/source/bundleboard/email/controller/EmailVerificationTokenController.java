package com.source.bundleboard.email.controller;

import com.source.bundleboard.email.service.EmailVerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class EmailVerificationTokenController {

    private EmailVerificationTokenService emailVerificationTokenService;


}
