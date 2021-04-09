/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package ch.ubique.notifyme.sdk.backend.ws.radarcovid.exception;

import org.springframework.http.HttpStatus;


public class RadarCovidServerException extends RuntimeException {

    private final HttpStatus httpStatus;

    /**
     * The Constructor for the Exception class.
     *
     * @param httpStatus the state of the server
     * @param message    the message
     */
    public RadarCovidServerException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
    
}
