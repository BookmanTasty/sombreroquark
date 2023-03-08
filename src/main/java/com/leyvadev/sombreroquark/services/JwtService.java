package com.leyvadev.sombreroquark.services;

import com.leyvadev.sombreroquark.model.SombreroUser;

public interface JwtService {
    String generateEmailConfirmationToken(SombreroUser user);
    String verifyEmailConfirmationToken(String token);
    String verifyRefreshToken(String token);
    String generateAccessToken(SombreroUser user);
    String generateRefreshToken(SombreroUser user);
    String generatePasswordResetToken(SombreroUser user);

}
