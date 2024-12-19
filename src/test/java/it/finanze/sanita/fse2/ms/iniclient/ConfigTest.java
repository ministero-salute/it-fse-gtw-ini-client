/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * Copyright (C) 2023 Ministero della Salute
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package it.finanze.sanita.fse2.ms.iniclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static it.finanze.sanita.fse2.ms.iniclient.client.routes.base.ClientRoutes.Config.*;
import static it.finanze.sanita.fse2.ms.iniclient.config.Constants.Profile.TEST;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles(TEST)
@TestInstance(PER_CLASS)
class ConfigTest extends AbstractConfig {

    private static final List<Pair<String, String>> DEFAULT_PROPS = Arrays.asList(
        Pair.of(PROPS_NAME_REMOVE_METADATA_ENABLE, "false"),
        Pair.of(PROPS_NAME_SUBJECT, "false"),
        Pair.of(PROPS_NAME_ISSUER_CF, "false"),
        Pair.of(PROPS_NAME_CONTROL_LOG_ENABLED, "false"),
        Pair.of(PROPS_NAME_KPI_LOG_ENABLED, "false")
    );

    @Test
    void testCacheProps() {
        testCacheProps(DEFAULT_PROPS.get(0), () -> assertFalse(config.isRemoveMetadataEnable()));
        testCacheProps(DEFAULT_PROPS.get(1), () -> assertFalse(config.isSubjectNotAllowed()));
        testCacheProps(DEFAULT_PROPS.get(2), () -> assertFalse(config.isCfOnIssuerNotAllowed()));
        testCacheProps(DEFAULT_PROPS.get(3), () -> assertFalse(config.isControlLogPersistenceEnable()));
        testCacheProps(DEFAULT_PROPS.get(4), () -> assertFalse(config.isKpiLogPersistenceEnable()));
    }

    @Test
    void testRefreshProps() {
        testRefreshProps(DEFAULT_PROPS.get(0), "true", () -> assertTrue(config.isRemoveMetadataEnable()));
        testRefreshProps(DEFAULT_PROPS.get(1), "true", () -> assertTrue(config.isSubjectNotAllowed()));
        testRefreshProps(DEFAULT_PROPS.get(2), "true", () -> assertTrue(config.isCfOnIssuerNotAllowed()));
        testRefreshProps(DEFAULT_PROPS.get(3), "true", () -> assertTrue(config.isControlLogPersistenceEnable()));
        testRefreshProps(DEFAULT_PROPS.get(4), "true", () -> assertTrue(config.isKpiLogPersistenceEnable()));
    }

    @Test
    void testIntegrityProps() {
        testIntegrityCheck();
    }

    @Override
    public List<Pair<String, String>> defaults() {
        return DEFAULT_PROPS;
    }
}
