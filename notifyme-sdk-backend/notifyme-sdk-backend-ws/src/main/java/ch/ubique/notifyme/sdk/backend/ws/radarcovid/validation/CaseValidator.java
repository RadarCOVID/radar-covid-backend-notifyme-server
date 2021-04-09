/*
 * Copyright (c) 2020 Gobierno de España
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */
package ch.ubique.notifyme.sdk.backend.ws.radarcovid.validation;

import ch.ubique.notifyme.sdk.backend.ws.radarcovid.etc.Constants;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.util.CheckSumUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CaseValidator implements ConstraintValidator<CaseConstraint, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        boolean result = false;

        if (StringUtils.isNotEmpty(value)) {
            result = CheckSumUtil.validateChecksum(value);
            if (result) {
                int ccaa1 = Character.getNumericValue(value.charAt(1));
                int ccaa2 = Character.getNumericValue(value.charAt(value.length() - 1));
                int ccaa = (ccaa1 * 10 + ccaa2);
                result = (ccaa >= Constants.CCAA_MIN) && (ccaa <= Constants.CCAA_MAX);
            }
        }

        return result;
    }



}
