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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaseDto implements Serializable {

	@NotNull
	@Size(min = 2, max = 2)
	private String ccaa;

	@NotNull
	private Long startTime;

	@NotNull
	private Long endTime;

	private String venue;

	public CaseDto() {
	}

	public CaseDto(String ccaa, Long startTime, Long endTime, String venue) {
		this.ccaa = ccaa;
		this.startTime = startTime;
		this.endTime = endTime;
		this.venue = venue;
	}

	public String getCcaa() {
		return ccaa;
	}

	public void setCcaa(String ccaa) {
		this.ccaa = ccaa;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	@Override
	public String toString() {
		return "CaseDto [ccaa=" + ccaa + ", startTime=" + startTime + ", endTime=" + endTime + ", venue=" + venue + "]";
	}

}
