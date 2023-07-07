package com.sunlifemalaysia.app4share.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.sunlifemalaysia.app4share.service.JwtService;

@Service
public class JwtServiceImpl implements JwtService {
    private static final Logger log = LoggerFactory.getLogger(JwtServiceImpl.class);
    private final JWTVerifier verifier;

    public JwtServiceImpl(@Value("${app.auth.jwt.secret}") final String secret) {
        this.verifier = JWT.require(Algorithm.HMAC512(secret)).build();
    }

    @Override
    public String validateTokenAndGetUsername(final String token) {
        try {
            return verifier.verify(token).getSubject();
        } catch (final JWTVerificationException verificationEx) {
            log.warn("token invalid: {}", verificationEx.getMessage());

        }

        return "";
    }

}