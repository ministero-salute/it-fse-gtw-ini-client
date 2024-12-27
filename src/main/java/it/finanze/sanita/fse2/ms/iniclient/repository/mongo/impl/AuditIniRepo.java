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
