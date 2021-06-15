package com.example.kafkastreamsinterceptors;

import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanCustomizer;

import static java.util.Collections.singletonList;
import static java.util.Map.of;
import static java.util.Objects.requireNonNull;
import static java.util.UUID.randomUUID;
import static org.apache.kafka.clients.consumer.ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.consumerPrefix;
import static org.apache.kafka.streams.StreamsConfig.producerPrefix;

@SpringBootApplication
@Slf4j
public class KafkaStreamsInterceptorsApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaStreamsInterceptorsApplication.class, args);
	}

	@Bean
	public java.util.function.Supplier<LocationSupplierSource> sendTestData(){
		return () -> generateSourceEntity();
	}

	@Bean
	public Function<KStream<String, LocationSupplierSource>, KStream<String, LocationSupplierSource>> concatFn() {
		return input -> input
				.peek((k, v) -> log.info("pre processed json message. key: {}  value: {} %n", k, v))
				.peek((k, v) -> log.info("post processed json message. key: {}  value: {} %n", k, v));
	}

	@Bean
	public StreamsBuilderFactoryBeanConfigurer streamsBuilderFactoryBeanCustomizer() {
		return factoryBean -> {
			factoryBean.setKafkaStreamsCustomizer(kafkaStreams -> kafkaStreams.setUncaughtExceptionHandler((thread, throwable) -> {
				log.error("Uncaught exception in stream {} {}", thread, throwable.getMessage());
			}));

			requireNonNull(factoryBean.getStreamsConfiguration()).putAll(
					of(
							consumerPrefix(INTERCEPTOR_CLASSES_CONFIG), singletonList(ACLConsumerInterceptor.class.getName())
					));
		};
	}


	public static LocationSupplierSource generateSourceEntity(){

		String id = randomUUID().toString();
		LocationSupplierSource.Group grp = LocationSupplierSource.Group
				.builder()
				.id(String.valueOf(id.length()))
				.name("Group_name")
				.build();

		return LocationSupplierSource.builder()
				.endDate("2018-07-14")
				.lastRevisionDate("2018-07-14")
				.name("name")
				.sid(id)
				.startDate("2018-07-14")
				.status("status")
				.supplierCode("supp code")
				.supplierNumber("100")
				.vatNumber("100")
				.group(grp)
				.build();
	}

}
