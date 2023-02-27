package com.leyvadev.sombreroquark.services;

import com.leyvadev.sombreroquark.model.SombreroUser;

public interface EmailService {
    void sendWelcomeEmail(SombreroUser user);
}
