package com.leyvadev.sombreroquark.services.impl;

import com.leyvadev.sombreroquark.clients.EmailTemplateClient;
import com.leyvadev.sombreroquark.model.SombreroUser;
import com.leyvadev.sombreroquark.services.EmailService;
import com.leyvadev.sombreroquark.services.JwtService;
import com.leyvadev.sombreroquark.utils.EmailTemplateParser;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.qute.Template;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.xml.transform.Templates;
import java.util.Map;

@ApplicationScoped
public class EmailServiceImpl implements EmailService {
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
        String verificationLink = sombreroquarkPublicUrl + quarkusHttpRootPath  + "api/users/verify/email?token=" + jwtService.generateEmailConfirmationToken(user) + "&redirect=" + redirect;
        values.put("verificationLink", verificationLink);
        String parsedTemplate = emailTemplateParser.parseTemplate(template, values);
        mailer.send(Mail.withHtml(user.getEmail(), "Verify your email", parsedTemplate));
    }
}
