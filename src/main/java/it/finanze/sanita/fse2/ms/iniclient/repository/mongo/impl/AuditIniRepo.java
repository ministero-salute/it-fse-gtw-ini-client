package it.finanze.sanita.fse2.ms.iniclient.repository.mongo.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import it.finanze.sanita.fse2.ms.iniclient.repository.entity.AuditIniETY;
import it.finanze.sanita.fse2.ms.iniclient.repository.mongo.IAuditIniRepo;

@Repository
public class AuditIniRepo implements IAuditIniRepo {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	
	@Override
	public AuditIniETY findByWii(String wii) {
		AuditIniETY out = null;
		Query query = new Query();
		query.addCriteria(Criteria.where("workflow_instance_id").is(wii));
		List<AuditIniETY> results = mongoTemplate.find(query, AuditIniETY.class);
		if(!results.isEmpty()) {
			out = results.get(0);			
		}
		return out;
	}

	@Override
	public void insert(AuditIniETY ety) {
		mongoTemplate.insert(ety);
	}


}
