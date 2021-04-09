/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package ch.ubique.notifyme.sdk.backend.model.radarcovid;

import java.time.Instant;

public class CaseEntity {

    private Integer id;

    private String ccaa;

    private String caseNumber;

    private boolean redeemed;

    private Instant createdAt;

    private Instant redeemedAt;
    
    private Instant startTime;
    
    private Instant endTime;
    
    private String venue;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCcaa() {
        return ccaa;
    }

    public void setCcaa(String ccaa) {
        this.ccaa = ccaa;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public boolean isRedeemed() {
        return redeemed;
    }

    public void setRedeemed(boolean redeemed) {
        this.redeemed = redeemed;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getRedeemedAt() {
        return redeemedAt;
    }

    public void setRedeemedAt(Instant redeemedAt) {
        this.redeemedAt = redeemedAt;
    }

	public Instant getStartTime() {
		return startTime;
	}

	public void setStartTime(Instant startTime) {
		this.startTime = startTime;
	}

	public Instant getEndTime() {
		return endTime;
	}

	public void setEndTime(Instant endTime) {
		this.endTime = endTime;
	}

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}
    
}
