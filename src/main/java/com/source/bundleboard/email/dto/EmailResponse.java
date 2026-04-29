package com.source.bundleboard.email.dto;

public record EmailResponse(

        Boolean success,

        String message,

        Integer attemptsLeft

) {
}
