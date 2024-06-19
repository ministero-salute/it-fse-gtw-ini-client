package it.finanze.sanita.fse2.ms.iniclient.service.impl;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier; 
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
 
import it.finanze.sanita.fse2.ms.iniclient.exceptions.base.BusinessException;
import it.finanze.sanita.fse2.ms.iniclient.service.IKafkaSRV;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka management service.
 */
@Service
@Slf4j
public class KafkaSRV implements IKafkaSRV {
  
	 
	/**
	 * Not transactional producer.
	 */
	@Autowired
	@Qualifier("notxkafkatemplate")
	protected KafkaTemplate<String, String> notxKafkaTemplate;

	    
	@Override
	public void sendMessage(String topic, String key, String value) {
		ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, value);
		try {
			kafkaSend(producerRecord);
		} catch (Exception e) {
			log.error("Send failed.", e);
			throw new BusinessException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private void kafkaSend(ProducerRecord<String, String> producerRecord) {
		notxKafkaTemplate.send(producerRecord);
	}
}