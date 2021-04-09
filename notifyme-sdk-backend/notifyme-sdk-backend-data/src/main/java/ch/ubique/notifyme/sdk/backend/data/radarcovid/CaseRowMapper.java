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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import ch.ubique.notifyme.sdk.backend.model.radarcovid.CaseEntity;

public class CaseRowMapper implements RowMapper<CaseEntity> {

    @Override
    public CaseEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
    	CaseEntity caseEntity = new CaseEntity();
    	caseEntity.setId(rs.getInt("pk_casecode_id"));
    	caseEntity.setCaseNumber(rs.getString("case_number"));
    	caseEntity.setCcaa(rs.getString("ccaa_id"));
    	caseEntity.setRedeemed(rs.getBoolean("redeemed"));
    	caseEntity.setCreatedAt(rs.getTimestamp("created_at").toInstant());
    	Timestamp tsRedeemedAt = rs.getTimestamp("redeemed_at");
    	if (tsRedeemedAt != null) {
    		caseEntity.setRedeemedAt(tsRedeemedAt.toInstant());
    	}
    	caseEntity.setStartTime(rs.getTimestamp("start_time").toInstant());
    	caseEntity.setEndTime(rs.getTimestamp("end_time").toInstant());
    	caseEntity.setVenue(rs.getString("venue"));
        return caseEntity;
    }
}
