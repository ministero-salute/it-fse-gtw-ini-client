
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
package it.finanze.sanita.fse2.ms.iniclient.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import it.finanze.sanita.fse2.ms.iniclient.config.Constants;
import it.finanze.sanita.fse2.ms.iniclient.utility.ProfileUtility;

@Configuration
public class CollectionNaming {
	
    @Autowired
    private ProfileUtility profileUtility;

    @Bean("iniEdsInvocationBean")
    public String getIniEdsInvocationCollection() {
        if (profileUtility.isTestProfile()) {
            return Constants.Profile.TEST_PREFIX + Constants.Collections.INI_EDS_INVOCATION;
        }
        return Constants.Collections.INI_EDS_INVOCATION;
    }

    @Bean("issuersBean")
    public String getIssuersCollection(){
        if (profileUtility.isTestProfile()){
            return Constants.Profile.TEST_PREFIX + Constants.Collections.ISSUERS;
        }
        return Constants.Collections.ISSUERS;
    }

    @Bean("auditIni")
    public String getAuditIniCollection() {
        if (profileUtility.isTestProfile()) {
            return Constants.Profile.TEST_PREFIX + Constants.Collections.AUDIT_INI;
        }
        return Constants.Collections.AUDIT_INI;
    }
}
