/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package ch.ubique.notifyme.sdk.backend.ws.radarcovid.controller;

import java.util.concurrent.Callable;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.ubique.notifyme.sdk.backend.ws.radarcovid.annotation.Loggable;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.model.CaseDto;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.model.CaseResponseDto;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.model.TokenResponseDto;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.model.VerifyCaseDto;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.security.CcaaAuthorizationUtil;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.service.CaseService;
import ch.ubique.openapi.docannotations.Documentation;

@Controller
@RequestMapping("/v1/casecode")
public class CaseController {

    private static final Logger logger = LoggerFactory.getLogger(CaseController.class);

    private final CaseService caseCodeService;

    public CaseController(CaseService caseCodeService) {
        this.caseCodeService = caseCodeService;
    }

    @Loggable
    @PostMapping
    @Documentation(
            description = "Endpoint used to retrieve a case code",
            responses = {
                    "200=>Case code generated",
                    "400=>Bad request",
                    "403=>Authentication failed"
            })
    public @ResponseBody
    Callable<ResponseEntity<CaseResponseDto>> generateCaseCode(
    		@Documentation(description = "The Code to be generated") @Valid @RequestBody CaseDto caseDto,
            @AuthenticationPrincipal
            @Documentation(
                    description = "JWT token that can be verified by the backend server")
                    Object principal) {
		CcaaAuthorizationUtil.isValid(principal, caseDto.getCcaa());
        String caseCode = caseCodeService.generateCaseCode(caseDto);
        CaseResponseDto response = new CaseResponseDto(caseCode);
        return () -> ResponseEntity.ok(response);
    }
    
    @Loggable
    @Documentation(
            description = "Endpoint used to verify a case code",
            responses = {
                    "200=>Case code is valid",
                    "400=>Bad request",
                    "403=>Authentication failed"
            })
    @PostMapping(value = "/verify",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @CrossOrigin(origins = { "https://test-radar.covid19.gob.es", "https://radarcovid.gob.es" })
    public Callable<ResponseEntity<TokenResponseDto>> verifyCaseCode(
    		@Documentation(description = "The Code to be verified") @Valid @RequestBody VerifyCaseDto verifyCaseDto) {
    	TokenResponseDto response = caseCodeService.redeemCaseCode(verifyCaseDto);
        logger.info("The Code {} is valid - JWT token {}", verifyCaseDto, response);
        return () -> ResponseEntity.ok(response);
    }

}
