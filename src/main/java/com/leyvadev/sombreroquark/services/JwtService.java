package com.leyvadev.sombreroquark.services;

import com.leyvadev.sombreroquark.model.SombreroUser;

public interface JwtService {
    String generateEmailConfirmationToken(SombreroUser user);
    String verifyEmailConfirmationToken(String token);
}
