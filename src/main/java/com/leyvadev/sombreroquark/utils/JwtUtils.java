package com.leyvadev.sombreroquark.utils;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jose4j.jwk.*;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver;
import org.jose4j.lang.JoseException;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class JwtUtils {
    private static final Logger LOGGER = Logger.getLogger(JwtUtils.class);
    @ConfigProperty(name = "mp.jwt.verify.publickey.location.google")
    String googlePublicKeyLocation;
    @ConfigProperty(name = "mp.jwt.verify.publickey.location.apple")
    String applePublicKeyLocation;
    @ConfigProperty(name = "mp.jwt.verify.issuer.google")
    String googleIssuer;
    @ConfigProperty(name = "mp.jwt.verify.issuer.apple")
    String appleIssuer;
    @ConfigProperty(name = "smallrye.jwt.sign.key")
    String privateKeyBase64;
    public Map<String, Object> getClaimsFromTokenProvider(String token, String provider) {
        String publicKeyLocation;
        if (provider.equals("google")) {
            publicKeyLocation = googlePublicKeyLocation;
        } else if (provider.equals("apple")) {
            publicKeyLocation = applePublicKeyLocation;
        } else {
            throw new IllegalArgumentException("Provider not allowed");
        }
        HttpsJwks httpsJwks = new HttpsJwks(publicKeyLocation);
        HttpsJwksVerificationKeyResolver httpsJwksVerificationKeyResolver = new HttpsJwksVerificationKeyResolver(httpsJwks);
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setVerificationKeyResolver(httpsJwksVerificationKeyResolver)
                .setSkipDefaultAudienceValidation()
                .setExpectedIssuers(true, googleIssuer, appleIssuer)
                .build();
        try {
            JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
            return jwtClaims.getClaimsMap();
        } catch (Exception e) {
            LOGGER.error("Token not valid", e);
            throw new IllegalArgumentException("Token not valid");
        }
    }

    public RsaJsonWebKey rsaJsonWebKeyPublic() {
        String privateKey = new String(java.util.Base64.getDecoder().decode(privateKeyBase64));
        try {
            return (RsaJsonWebKey) PublicJsonWebKey.Factory.newPublicJwk(privateKey);
        } catch (JoseException e) {
            LOGGER.error("Error creating public key", e);
        }
        throw new IllegalArgumentException("Error retrieving JWT_JWKS_KEY");
    }
}
