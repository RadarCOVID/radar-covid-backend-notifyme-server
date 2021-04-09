/*
 * Copyright (c) 2020 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package ch.ubique.notifyme.sdk.backend.ws.config;

import java.util.Base64;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import ch.ubique.notifyme.sdk.backend.data.radarcovid.CaseDataService;
import ch.ubique.notifyme.sdk.backend.data.radarcovid.JdbcCaseDataServiceImpl;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.common.handler.SediaExceptionHandler;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.controller.CaseController;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.etc.Constants;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.security.JwtGenerator;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.security.impl.JwtGeneratorImpl;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.service.CaseService;
import ch.ubique.notifyme.sdk.backend.ws.radarcovid.service.impl.CaseServiceImpl;
import ch.ubique.notifyme.sdk.backend.ws.security.KeyVault;
import ch.ubique.notifyme.sdk.backend.ws.security.KeyVault.PrivateKeyNoSuitableEncodingFoundException;
import ch.ubique.notifyme.sdk.backend.ws.security.KeyVault.PublicKeyNoSuitableEncodingFoundException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Configuration
@Profile({ "radarcovid-local", "radarcovid-pre", "radarcovid-pro" })
public class WSRadarCovidConfig extends WSBaseConfig {

	@Value("${datasource.username}")
	String dataSourceUser;

	@Value("${datasource.password}")
	String dataSourcePassword;

	@Value("${datasource.url}")
	String dataSourceUrl;

	@Value("${datasource.schema:}")
	String dataSourceSchema;

	@Value("${datasource.driverClassName}")
	String dataSourceDriver;

	@Value("${datasource.failFast}")
	String dataSourceFailFast;

	@Value("${datasource.minimumIdle}")
	int dataSourceMinimumIdle;

	@Value("${datasource.maximumPoolSize}")
	int dataSourceMaximumPoolSize;

	@Value("${datasource.maxLifetime}")
	int dataSourceMaxLifetime;

	@Value("${datasource.idleTimeout}")
	int dataSourceIdleTimeout;

	@Value("${datasource.connectionTimeout}")
	int dataSourceConnectionTimeout;

	@Value("${application.case-code.max-length:12}")
	private int caseCodeMaxLength;
	
	@Value("${ws.app.jwt.privatekey:}")
	private String privateKey;

	@Value("${ws.app.jwt.publickey:}")
	public String publicKey;
	
	@Value("${ws.app.jwt.algorithm:}")
	public String algorithm;

	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		HikariConfig config = new HikariConfig();
		Properties props = new Properties();
		props.put("url", dataSourceUrl);
		props.put("user", dataSourceUser);
		props.put("password", dataSourcePassword);
		if (StringUtils.isNotEmpty(dataSourceSchema))
			config.setSchema(dataSourceSchema);
		config.setDataSourceProperties(props);
		config.setDataSourceClassName(dataSourceDriver);
		config.setMinimumIdle(dataSourceMinimumIdle);
		config.setMaximumPoolSize(dataSourceMaximumPoolSize);
		config.setMaxLifetime(dataSourceMaxLifetime);
		config.setIdleTimeout(dataSourceIdleTimeout);
		config.setConnectionTimeout(dataSourceConnectionTimeout);
		return new HikariDataSource(config);
	}

	@Bean
	@ConditionalOnProperty(name = "datasource.flyway.load", havingValue = "true", matchIfMissing = true)
	@Override
	public Flyway flyway() {
		Flyway flyWay = Flyway.configure().dataSource(dataSource()).locations("classpath:/db/migration/pgsql").load();
		flyWay.migrate();
		return flyWay;
	}

	@Bean
	@ConditionalOnProperty(name = "datasource.flyway.load", havingValue = "false", matchIfMissing = true)
	public Flyway flywayNoLoad() {
		Flyway flyWay = Flyway.configure().dataSource(dataSource()).locations("classpath:/db/migration/pgsql").load();
		return flyWay;
	}

	@Override
	public String getDbType() {
		return "pgsql";
	}

	@Bean
	public CaseDataService caseDataService(DataSource dataSource) {
		return new JdbcCaseDataServiceImpl(dataSource);
	}
	
	@Bean
	public JwtGenerator jwtGenerator(KeyVault keyVault) {
		return new JwtGeneratorImpl(keyVault);
	}

	@Bean
	public CaseService caseService(CaseDataService dataService, JwtGenerator jwtGenerator) {
		return new CaseServiceImpl(dataService, caseCodeMaxLength, jwtGenerator);
	}

	@Bean
	public CaseController caseController(CaseService caseCodeService) {
		return new CaseController(caseCodeService);
	}

	@Bean
	public KeyVault keyVault() {
		var privateKey = getPrivateKey();
		var publicKey = getPublicKey();
		
		if (privateKey.isEmpty() || publicKey.isEmpty()) {
			var kp = Keys.keyPairFor(SignatureAlgorithm.ES256);
			var traceKp = new KeyVault.KeyVaultKeyPair(Constants.PAIR_KEY_TRACE, kp);
			return new KeyVault(traceKp);
		}
		
		var trace = new KeyVault.KeyVaultEntry(Constants.PAIR_KEY_TRACE, getPrivateKey(), getPublicKey(), algorithm);
		try {
			return new KeyVault(trace);
		} catch (PrivateKeyNoSuitableEncodingFoundException | PublicKeyNoSuitableEncodingFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	String getPrivateKey() {
		return new String(Base64.getDecoder().decode(privateKey));
	}

	String getPublicKey() {
		return new String(Base64.getDecoder().decode(publicKey));
	}
	
	@Bean
	public SediaExceptionHandler sediaExceptionHandler() {
		return new SediaExceptionHandler();
	}

}
