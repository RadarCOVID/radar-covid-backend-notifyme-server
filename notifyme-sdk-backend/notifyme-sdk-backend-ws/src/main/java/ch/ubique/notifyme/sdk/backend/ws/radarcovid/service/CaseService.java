/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package ch.ubique.notifyme.sdk.backend.ws.radarcovid.service;

import ch.ubique.notifyme.sdk.backend.ws.radarcovid.model.CaseDto;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.model.TokenResponseDto;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.model.VerifyCaseDto;

public interface CaseService {

    String generateCaseCode(CaseDto caseDto);

    TokenResponseDto redeemCaseCode(VerifyCaseDto verifyCaseDto);

}
