package com.leyvadev.sombreroquark.services;

import com.leyvadev.sombreroquark.model.SombreroUser;

public interface EmailService {
    void sendWelcomeEmail(SombreroUser user);
    void sendEmailConfirmation(SombreroUser user, String redirect);
    void sendEmailMagicLink(SombreroUser user, String redirect);
    void sendResetPasswordEmail(SombreroUser user, String redirect);
}
