package com.leyvadev.sombreroquark.services.impl;

import com.leyvadev.sombreroquark.clients.EmailTemplateClient;
import com.leyvadev.sombreroquark.model.SombreroUser;
import com.leyvadev.sombreroquark.services.EmailService;
import com.leyvadev.sombreroquark.services.JwtService;
import com.leyvadev.sombreroquark.utils.EmailTemplateParser;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;

@ApplicationScoped
public class EmailServiceImpl implements EmailService {

    private static final String REDIRECT = "&redirect=";
    @Inject
    Mailer mailer;
    @Inject
    EmailTemplateClient emailTemplateClient;
    @Inject
    EmailTemplateParser emailTemplateParser;
    @Inject
    JwtService jwtService;

    @ConfigProperty(name = "remote.template.welcome.url")
    String welcomeTemplateUrl;
    @ConfigProperty(name = "remote.template.verify-email.url")
    String verifyEmailTemplateUrl;
    @ConfigProperty(name = "remote.template.magic-link.url")
    String magicLinkTemplateUrl;
    @ConfigProperty(name = "remote.template.reset-password.url")
    String resetPasswordTemplateUrl;
    @ConfigProperty(name = "sombreroquark.public.url")
    String sombreroquarkPublicUrl;
    @ConfigProperty(name = "quarkus.http.root-path")
    String quarkusHttpRootPath;



    @Override
    public void sendWelcomeEmail(SombreroUser user) {
        String template = emailTemplateClient.downloadTemplate(welcomeTemplateUrl);
        Map<String, String> values = emailTemplateParser.getVariableValuesFromSombreroUserAsMap(template, user);
        String parsedTemplate = emailTemplateParser.parseTemplate(template, values);
        mailer.send(Mail.withHtml(user.getEmail(), "Welcome to Sombrero", parsedTemplate));
    }

    @Override
    public void sendEmailConfirmation(SombreroUser user, String redirect) {
        String template = emailTemplateClient.downloadTemplate(verifyEmailTemplateUrl);
        Map<String, String> values = emailTemplateParser.getVariableValuesFromSombreroUserAsMap(template, user);
        String verificationLink = sombreroquarkPublicUrl + quarkusHttpRootPath  + "api/users/verify/email?token=" + jwtService.generateEmailConfirmationToken(user) + REDIRECT + redirect;
        values.put("verificationLink", verificationLink);
        String parsedTemplate = emailTemplateParser.parseTemplate(template, values);
        mailer.send(Mail.withHtml(user.getEmail(), "Verify your email", parsedTemplate));
    }

    @Override
    public void sendEmailMagicLink(SombreroUser user, String redirect) {
        String template = emailTemplateClient.downloadTemplate(magicLinkTemplateUrl);
        Map<String, String> values = emailTemplateParser.getVariableValuesFromSombreroUserAsMap(template, user);
        String magicLink = sombreroquarkPublicUrl + quarkusHttpRootPath  + "api/auth/login/magic?token=" + jwtService.generateEmailConfirmationToken(user) + REDIRECT + redirect;
        values.put("magicLink", magicLink);
        String parsedTemplate = emailTemplateParser.parseTemplate(template, values);
        mailer.send(Mail.withHtml(user.getEmail(), "Login with magic link", parsedTemplate));
    }

    @Override
    public void sendResetPasswordEmail(SombreroUser user, String redirect) {
        String template = emailTemplateClient.downloadTemplate(resetPasswordTemplateUrl);
        Map<String, String> values = emailTemplateParser.getVariableValuesFromSombreroUserAsMap(template, user);
        String resetLink = sombreroquarkPublicUrl + quarkusHttpRootPath  + "api/users/reset/password?token=" + jwtService.generateResetPasswordToken(user) + REDIRECT + redirect;
        values.put("resetLink", resetLink);
        String parsedTemplate = emailTemplateParser.parseTemplate(template, values);
        mailer.send(Mail.withHtml(user.getEmail(), "Reset your password", parsedTemplate));
    }
}
