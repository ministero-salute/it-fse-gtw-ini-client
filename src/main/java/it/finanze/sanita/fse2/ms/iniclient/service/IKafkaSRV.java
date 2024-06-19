package it.finanze.sanita.fse2.ms.iniclient.service;
 
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * Interface for service used to handle kafka communications
 */
public interface IKafkaSRV {

	/**
	 * Send message over kafka topic
	 * @param topic
	 * @param key
	 * @param value
	 * @param trans
	 * @return
	 */
	void sendMessage(String topic, String key, String value);

}