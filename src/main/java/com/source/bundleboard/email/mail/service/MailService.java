package com.source.bundleboard.email.mail.service;

import java.util.Map;

public interface MailService {

    void sendTemplateEmailSync(String toEmail, String subject, String template, Map<String, Object> model);

    String buildLink(String path, String token);

}
