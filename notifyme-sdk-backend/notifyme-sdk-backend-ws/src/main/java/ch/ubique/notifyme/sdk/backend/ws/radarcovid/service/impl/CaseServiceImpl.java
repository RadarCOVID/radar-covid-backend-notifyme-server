/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package ch.ubique.notifyme.sdk.backend.ws.radarcovid.service.impl;

import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import ch.ubique.notifyme.sdk.backend.data.radarcovid.CaseDataService;
import ch.ubique.notifyme.sdk.backend.model.radarcovid.CaseEntity;
import ch.ubique.notifyme.sdk.backend.model.util.DateUtil;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.annotation.Loggable;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.etc.Constants;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.exception.RadarCovidServerException;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.model.CaseDto;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.model.TokenResponseDto;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.model.VerifyCaseDto;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.security.JwtGenerator;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.service.CaseService;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.util.CheckSumUtil;

public class CaseServiceImpl implements CaseService {

    private static final int MIN_LENGTH = 6; // ccaa (2 digits) + checksum (1 digit) + 3 (milliseconds)
    private static final int MILLISECONDS_LENGTH = 3;
    private static final int MAGIC_NUMBER = 11235813;

    private final CaseDataService caseDataService;
    private final int caseCodeMaxLength;
    private final JwtGenerator jwtGenerator;

    public CaseServiceImpl(CaseDataService caseDataService, int caseCodeMaxLength, JwtGenerator jwtGenerator) {
        this.caseDataService = caseDataService;
        this.caseCodeMaxLength = caseCodeMaxLength;
        this.jwtGenerator = jwtGenerator;
    }

    @Loggable
    @Override
    public String generateCaseCode(CaseDto caseDto) {
    	String ccaa = caseDto.getCcaa();
    	Integer ccaaIntValue = StringUtils.isNumeric(ccaa) ? Integer.valueOf(ccaa) : null;
        if (ccaaIntValue != null && ccaaIntValue >= Constants.CCAA_MIN && ccaaIntValue <= Constants.CCAA_MAX) {
            long timestamp = System.currentTimeMillis();
            int randomLength = caseCodeMaxLength - MIN_LENGTH;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ccaa.charAt(0));
            if (randomLength < caseCodeMaxLength)
                stringBuilder.append(RandomStringUtils.randomAlphanumeric(randomLength));
            stringBuilder.append(lastNDigits(((timestamp % 1000) * MAGIC_NUMBER ) % 1000, MILLISECONDS_LENGTH));
            stringBuilder.append(ccaa.charAt(1));
            String caseNumber = CheckSumUtil.addCheckSum(stringBuilder.toString());
            saveCaseNumber(caseDto, caseNumber);
            return caseNumber;
        }
        throw new RadarCovidServerException(HttpStatus.BAD_REQUEST, "Case " + caseDto + " is invalid");
    }

    private String lastNDigits(String str, int lastDigits) {
    	String subStr = str != null ? str.substring(Math.max(0, str.length() - lastDigits)) : "";
    	return StringUtils.leftPad(subStr, lastDigits, "0");
    }

    private String lastNDigits(long number, int lastDigits) {
        return lastNDigits(String.valueOf(number), lastDigits);
    }

    private void saveCaseNumber(CaseDto caseDto, String caseNumber) {
        CaseEntity caseEntity = new CaseEntity();
        caseEntity.setCcaa(caseDto.getCcaa());
        caseEntity.setCaseNumber(caseNumber);
        caseEntity.setStartTime(DateUtil.toInstant(caseDto.getStartTime()));
        caseEntity.setEndTime(DateUtil.toInstant(caseDto.getEndTime()));
        caseEntity.setVenue(caseDto.getVenue());
        caseDataService.insertCaseCode(caseEntity);
    }

	@Loggable
	@Override
	public TokenResponseDto redeemCaseCode(VerifyCaseDto verifyCaseDto) {
		if (verifyCaseDto != null 
				&& !StringUtils.isEmpty(verifyCaseDto.getCode())
				&& CheckSumUtil.validateChecksum(verifyCaseDto.getCode())) {
			return findCaseCodeNotRedeem(verifyCaseDto.getCode())
					.map(c -> {
						String token = jwtGenerator.generateJwt(verifyCaseDto.getCode());
						Long startTime = DateUtil.toEpochMilli(c.getStartTime());
						Long endTime = DateUtil.toEpochMilli(c.getEndTime());
						return new TokenResponseDto(token, startTime, endTime);
					}).orElseThrow(() -> new RadarCovidServerException(HttpStatus.BAD_REQUEST,
							"Case " + verifyCaseDto + " is redeemed"));
		}
		throw new RadarCovidServerException(HttpStatus.BAD_REQUEST, "Case " + verifyCaseDto + " is invalid");
	}

    private Optional<CaseEntity> findCaseCodeNotRedeem(String caseCode) {
    	return caseDataService.findCasesByCaseCodeNotRedeemed(caseCode).stream()
				.findFirst()
				.map(c -> {
					caseDataService.redeemCaseCode(c.getCaseNumber());
					return c;
				});
    }
}
