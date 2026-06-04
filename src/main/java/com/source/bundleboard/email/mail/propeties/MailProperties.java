package com.source.bundleboard.email.mail.propeties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "app.mail")
public class MailProperties {

    private String from;

    private String fromName;

    private Templates templates = new Templates();

    private Subjects subjects = new Subjects();

    private Paths paths = new Paths();

    @Getter
    @Setter
    public static class Templates {

        private String verificationEmail;

        private String resetPassword;

        private String purchaseReceipt;

    }

    @Getter
    @Setter
    public static class Subjects {

        private String verificationEmail;

        private String resetPassword;

        private String purchaseReceipt;

    }

    @Getter
    @Setter
    public static class Paths {

        private String verificationEmail;

        private String resetPassword;

        private String purchaseReceipt;

    }


}
