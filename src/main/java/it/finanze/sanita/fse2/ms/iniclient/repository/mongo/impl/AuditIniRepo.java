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

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import it.finanze.sanita.fse2.ms.iniclient.repository.entity.AuditIniETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.IAuditIniRepo;

import static it.finanze.sanita.fse2.ms.iniclient.repository.entity.AuditIniETY.*;

@Repository
@Slf4j
public class AuditIniRepo implements IAuditIniRepo {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	
	@Override
	public List<AuditIniETY> findByWii(final String wii) {
		Query query = new Query();
		query.addCriteria(Criteria.where(WORKFLOW_INSTANCE_ID).is(wii));
		return mongoTemplate.find(query, AuditIniETY.class);
	}

	@Override
	public void insert(AuditIniETY ety) {
		mongoTemplate.insert(ety);
	}

	@Override
	public void updateResponseByWiiAndEventType(String wii, String eventType,String response) {
		Query query = new Query();
		query.addCriteria(Criteria.where(WORKFLOW_INSTANCE_ID).is(wii).and(EVENT_TYPE).is(eventType));

		Update update = new Update();
		update.set(SOAP_RESPONSE, response);

		mongoTemplate.updateFirst(query, update, AuditIniETY.class);
	}
}
