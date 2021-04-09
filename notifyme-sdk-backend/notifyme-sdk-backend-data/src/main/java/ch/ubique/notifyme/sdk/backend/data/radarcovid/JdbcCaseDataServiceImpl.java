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

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;

import ch.ubique.notifyme.sdk.backend.model.radarcovid.CaseEntity;
import ch.ubique.notifyme.sdk.backend.model.util.DateUtil;

public class JdbcCaseDataServiceImpl implements CaseDataService {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert caseCodeInsert;

    public JdbcCaseDataServiceImpl(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.caseCodeInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("t_radarcovid_casecodes")
                .usingGeneratedKeyColumns("pk_casecode_id");
    }

    @Override
    @Transactional
    public void insertCaseCode(CaseEntity caseEntity) {
        caseCodeInsert.execute(getCaseCodeParams(caseEntity));
    }

    private MapSqlParameterSource getCaseCodeParams(CaseEntity caseEntity) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("pk_casecode_id", caseEntity.getId());
        params.addValue("ccaa_id", caseEntity.getCcaa());
        params.addValue("case_number", caseEntity.getCaseNumber());
        params.addValue("created_at", new Date());
        params.addValue("redeemed", false);
        params.addValue("start_time", DateUtil.toDate(caseEntity.getStartTime()));
        params.addValue("end_time", DateUtil.toDate(caseEntity.getEndTime()));
        params.addValue("venue", caseEntity.getVenue());
        return params;
    }

	@Override
	public List<CaseEntity> findCasesByCaseCodeNotRedeemed(String caseCode) {
        var sql = "select * from t_radarcovid_casecodes "
        		+ "where case_number = :caseNumber and redeemed is false";
        final var params = new MapSqlParameterSource();
        params.addValue("caseNumber", caseCode);
        return jdbcTemplate.query(sql, params, new CaseRowMapper());
		
	}

	@Override
	@Transactional
	public int redeemCaseCode(String caseCode) {
		var sql = "update t_radarcovid_casecodes "
				+ "set redeemed = true, redeemed_at = :redeemed_at "
				+ "where case_number = :caseNumber";
        final var params = new MapSqlParameterSource();
        params.addValue("redeemed_at", new Date());
        params.addValue("caseNumber", caseCode);
        return jdbcTemplate.update(sql, params);
		
	}
}
