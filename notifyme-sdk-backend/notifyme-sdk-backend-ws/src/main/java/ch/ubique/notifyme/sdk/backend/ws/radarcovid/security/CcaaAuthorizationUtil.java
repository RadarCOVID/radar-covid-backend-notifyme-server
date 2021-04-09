/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package ch.ubique.notifyme.sdk.backend.ws.radarcovid.security;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;

import ch.ubique.notifyme.sdk.backend.ws.radarcovid.exception.RadarCovidServerException;

public final class CcaaAuthorizationUtil {
	
	private static final String CLAIM_CCAA = "ccaa";
	private static final String CLAIM_AUTHORITIES = "authorities";
	private static final String CLAIM_AUTHORITIE_ADMIN = "ROLE_ADMIN";
	
	public static final Optional<Jwt> getJwtFromPrincipal(Object principal) {
		if (principal != null && principal instanceof Jwt) {
			Jwt token = (Jwt) principal;
			return Optional.of(token);
		}
		return Optional.empty();
	}

	public static final Optional<String> getCcaaFromPrincipal(Object principal) {
		Optional<Jwt> jwt = getJwtFromPrincipal(principal);
		return jwt.filter(token -> Boolean.TRUE.equals(token.containsClaim(CLAIM_CCAA)))
				  .map(token -> token.getClaim(CLAIM_CCAA));
	}
	
	public static boolean isAdminFromPrincipal(Object principal) {
		Optional<Jwt> jwt = getJwtFromPrincipal(principal);
		return jwt.filter(token -> Boolean.TRUE.equals(token.containsClaim(CLAIM_AUTHORITIES)))
				  .filter(token -> token.getClaimAsStringList(CLAIM_AUTHORITIES).contains(CLAIM_AUTHORITIE_ADMIN))
				  .isPresent();
	}
	
	public static boolean isValid(Object principal, String ccaa) {
		if (isAdminFromPrincipal(principal)) {
			return true;
		}
		String principalCcaa = getCcaaFromPrincipal(principal).orElseThrow(
				() -> new RadarCovidServerException(HttpStatus.BAD_REQUEST, "Principal not valid: CCAA not found"));
		if (!principalCcaa.equals(ccaa)) {
			throw new RadarCovidServerException(HttpStatus.BAD_REQUEST, "Principal not valid: belongs to CCAA "
					+ principalCcaa + " and cannot create codes of CCAA " + ccaa);
		}
		return true;
	}
	
}
