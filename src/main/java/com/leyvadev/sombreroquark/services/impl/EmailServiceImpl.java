package com.leyvadev.sombreroquark.services.impl;

import com.leyvadev.sombreroquark.model.SombreroUser;
import com.leyvadev.sombreroquark.services.EmailService;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
@ApplicationScoped
public class EmailServiceImpl implements EmailService {
    @Inject
    Mailer mailer;
    @Override
    public void sendWelcomeEmail(SombreroUser user) {
        mailer.send(Mail.withText(user.getEmail(), "Welcome to Sombrero Quark", "Welcome to Sombrero Quark"));
    }
}
