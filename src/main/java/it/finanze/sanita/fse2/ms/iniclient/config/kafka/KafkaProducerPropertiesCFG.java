package it.finanze.sanita.fse2.ms.iniclient.config.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 *	Kafka producer properties configuration.
 */
@Data
@Component
public class KafkaProducerPropertiesCFG {
    
	/**
	 * Client id.
	 */
	@Value("${kafka.producer.client-id}")
	private String clientId;
	
	/**
	 * Retries.
	 */
	@Value("${kafka.producer.retries}")
	private Integer retries;
	
	/**
	 * Key serializer.
	 */
	@Value("${kafka.producer.key-serializer}")
	private String keySerializer;
	
	/**
	 * Value serializer.
	 */
	@Value("${kafka.producer.value-serializer}")
	private String valueSerializer;
	
	 
}