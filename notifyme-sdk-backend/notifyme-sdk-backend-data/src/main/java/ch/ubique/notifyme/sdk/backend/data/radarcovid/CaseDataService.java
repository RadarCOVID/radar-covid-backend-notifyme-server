/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package ch.ubique.notifyme.sdk.backend.data.radarcovid;

import java.util.List;

import ch.ubique.notifyme.sdk.backend.model.radarcovid.CaseEntity;

public interface CaseDataService {

    public void insertCaseCode(CaseEntity caseEntity);
    public List<CaseEntity> findCasesByCaseCodeNotRedeemed(String caseCode);
    public int redeemCaseCode(String caseCode);

}
