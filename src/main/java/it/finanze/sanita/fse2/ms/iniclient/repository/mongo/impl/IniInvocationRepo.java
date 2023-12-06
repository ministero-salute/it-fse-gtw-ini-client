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
package it.finanze.sanita.fse2.ms.iniclient.repository.mongo.impl;

import com.mongodb.client.result.UpdateResult;
import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.IIniInvocationRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import static it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY.FIELD_METADATA;
import static it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY.FIELD_WIF;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
@Slf4j
public class IniInvocationRepo implements IIniInvocationRepo {

	@Autowired
	private MongoTemplate mongo;

	@Override
	public IniEdsInvocationETY findByWorkflowInstanceId(final String wif) {
		IniEdsInvocationETY out;
		Query query = new Query(where(FIELD_WIF).is(wif));
		try {
			out = mongo.findOne(query, IniEdsInvocationETY.class);
		} catch(Exception ex) {
			log.error("Error while running find by workflow instance id query : " , ex);
			throw new BusinessException("Error while running find by workflow instance id query : " , ex);
		}
		return out;
	}

	@Override
	public boolean removeMetadataByWorkflowInstanceId(String wif) {
		Query search = new Query(where(FIELD_WIF).is(wif));
		Update op = new Update().unset(FIELD_METADATA);
		UpdateResult res;
		try {
			res = mongo.updateFirst(search, op, IniEdsInvocationETY.class);
		} catch (Exception ex) {
			log.error("Unable to remove '{}' field from wif '{}' due to {}", FIELD_METADATA, wif, ex.getMessage());
			throw new BusinessException(ex);
		}
		return res.getModifiedCount() == 1;
	}
}
