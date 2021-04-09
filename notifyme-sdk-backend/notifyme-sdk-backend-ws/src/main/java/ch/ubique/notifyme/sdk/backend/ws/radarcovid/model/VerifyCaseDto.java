/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package ch.ubique.notifyme.sdk.backend.ws.radarcovid.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import ch.ubique.notifyme.sdk.backend.ws.radarcovid.validation.CaseConstraint;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerifyCaseDto implements Serializable {

	@CaseConstraint(message = "Invalid checksum for code ${validatedValue}")
	private String code;

	public VerifyCaseDto() {
	}

	public VerifyCaseDto(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	@Override
	public String toString() {
		return "CaseDto [code=" + code + "]";
	}

}
