/*
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package it.finanze.sanita.fse2.ms.iniclient.repository.mongo.impl;

import it.finanze.sanita.fse2.ms.iniclient.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.repository.entity.IniEdsInvocationETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.IIniInvocationRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class IniInvocationRepo implements IIniInvocationRepo {

	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = 7040678303037387997L;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public IniEdsInvocationETY findByWorkflowInstanceId(final String workflowInstanceId) {
		IniEdsInvocationETY out = null;
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("workflow_instance_id").is(workflowInstanceId));
			out = mongoTemplate.findOne(query, IniEdsInvocationETY.class);
		} catch(Exception ex) {
			log.error("Error while running find by workflow instance id query : " , ex);
			throw new BusinessException("Error while running find by workflow instance id query : " , ex);
		}
		return out;
	}
	 
	 
}
