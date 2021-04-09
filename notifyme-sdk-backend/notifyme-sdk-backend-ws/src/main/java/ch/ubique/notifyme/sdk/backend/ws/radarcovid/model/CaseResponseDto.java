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

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaseResponseDto implements Serializable {
	
    private final String caseCode;

    public CaseResponseDto(String caseCode) {
        this.caseCode = caseCode;
    }

    public String getCaseCode() {
        return caseCode;
    }

}
