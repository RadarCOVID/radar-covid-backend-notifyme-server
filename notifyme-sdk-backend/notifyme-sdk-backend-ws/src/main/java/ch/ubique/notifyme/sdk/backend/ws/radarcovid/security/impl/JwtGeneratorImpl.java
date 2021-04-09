/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package ch.ubique.notifyme.sdk.backend.ws.radarcovid.security.impl;

import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ch.ubique.notifyme.sdk.backend.ws.radarcovid.etc.Constants;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.security.JwtGenerator;
import ch.ubique.notifyme.sdk.backend.ws.security.KeyVault;
import io.jsonwebtoken.Jwts;

@Component
public class JwtGeneratorImpl implements JwtGenerator {

    private static final String CLAIM_SCOPE_NAME = "scope";
    private static final String CLAIM_SCOPE_VALUE = "traceKey";

    @Value("${ws.app.jwt.issuer}")
    private String jwtIssuer;
    
    @Value("${ws.app.jwt.maxValidityMinutes}")
    private int maxValidityMinutes;

    private final KeyVault keyVault;

    public JwtGeneratorImpl(KeyVault keyVault) {
		this.keyVault = keyVault;
	}

    @Override
    public String generateJwt(String caseCode) {
        KeyPair keyPair = keyVault.get(Constants.PAIR_KEY_TRACE);
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
        
        String jwtId = UUID.randomUUID().toString();
        Date issuedAt = new Date();
        Date expiration = Date.from(Instant.now().plus(maxValidityMinutes, ChronoUnit.MINUTES));
        
        return Jwts.builder()
        		.setId(jwtId)
        		.setSubject(caseCode)
        		.setIssuer(jwtIssuer)
        		.setIssuedAt(issuedAt)
        		.setExpiration(expiration)
        		.claim(CLAIM_SCOPE_NAME, CLAIM_SCOPE_VALUE)
        		.signWith(privateKey)
        		.compact();
    }

}
