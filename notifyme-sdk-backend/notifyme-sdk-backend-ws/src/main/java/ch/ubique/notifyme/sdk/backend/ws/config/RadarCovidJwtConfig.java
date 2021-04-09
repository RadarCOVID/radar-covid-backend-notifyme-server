/*
 * Copyright (c) 2020 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package ch.ubique.notifyme.sdk.backend.ws.config;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Duration;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;

import ch.ubique.notifyme.sdk.backend.ws.security.KeyVault;
import ch.ubique.notifyme.sdk.backend.ws.security.KeyVault.PublicKeyNoSuitableEncodingFoundException;
import ch.ubique.notifyme.sdk.backend.ws.security.NotifyMeJwtDecoder;
import ch.ubique.notifyme.sdk.backend.ws.security.NotifyMeJwtValidator;

@Configuration
@EnableWebSecurity
@Profile({ "radarcovid-local", "radarcovid-pre", "radarcovid-pro" })
public class RadarCovidJwtConfig {
		
	@Order(1)
    public static class RadarCovidJWTBase extends WebSecurityConfigurerAdapter {
		
        @Value("${application.case-code.jwt.publickey}")
        String publicKey;
        
        @Value("${application.case-code.jwt.algorithm:EC}")
        String algorithm;

        @Value("${application.case-code.jwt.maxValidityMinutes: 360}")
        int maxValidityMinutes;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .antMatcher("/v1/casecode")
                    .csrf()
                    .disable()
                    .cors()
                    .and()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/v1/casecode")
                    .authenticated()
                    .anyRequest()
                    .permitAll()
                    .and()
                    .oauth2ResourceServer()
                    .jwt()
                    .decoder(radarJwtDecoder());
        }
        
        @Bean
        public NotifyMeJwtValidator radarJwtValidator() {
            return new NotifyMeJwtValidator("login", Duration.ofMinutes(maxValidityMinutes));
        }

        public JwtDecoder radarJwtDecoder()
                throws InvalidKeySpecException, NoSuchAlgorithmException, IOException,
                        PublicKeyNoSuitableEncodingFoundException {
            NotifyMeJwtDecoder jwtDecoder =
                    new NotifyMeJwtDecoder(KeyVault.loadPublicKey(getPublicKey(), algorithm));

            OAuth2TokenValidator<Jwt> defaultValidators = JwtValidators.createDefault();
            jwtDecoder.setJwtValidator(
                    new DelegatingOAuth2TokenValidator<>(defaultValidators, radarJwtValidator()));
            return jwtDecoder;
        }
        
        private String getPublicKey() {
            return new String(Base64.getDecoder().decode(publicKey));
        }
    }

}
