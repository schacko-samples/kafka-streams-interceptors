package com.example.kafkastreamsinterceptors;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Map;

@Slf4j
public class ACLConsumerInterceptor <K, V> implements ConsumerInterceptor<String, Object>{

	private static final String LOCATION_SUPPLIER_SOURCE = "my-foo";

	public void configure(final Map<String, ?> configs) {
		final Map<String, Object> copyConfigs = (Map<String, Object>) configs;
	}

	@Override
	public ConsumerRecords<String, Object> onConsume(ConsumerRecords<String, Object> records) {
		log.info("---<< ProducerRecord being received {} >>---", records);
		for (ConsumerRecord<String, Object> record : records) {
			if (record.topic().equals(LOCATION_SUPPLIER_SOURCE)) {
				log.info("---<< setting timestamp for  >>---");
				log.info("Received Message: timestamp ={}, partition ={}, offset = {}, key = {}, value = {}\n",
						record.timestamp(), record.partition(), record.offset(), record.key(), record.value().getClass().getName());
			}
		}
		return records;
	}

	public void onCommit(Map<TopicPartition, OffsetAndMetadata> offsets) {
		System.out.println("onCommit");
	}

	@Override
	public void close() {
		// no-op
	}
}
