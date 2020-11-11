package com.sibyg.demo.zipkin.config;

import brave.Tracer;
import brave.baggage.BaggageField;
import brave.kafka.streams.KafkaStreamsTracing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Configuration
@EnableKafka
@RequiredArgsConstructor
@Slf4j
@EnableKafkaStreams
public class KafkaStreamsConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsConfig.class);

    private static final String SERVICE_ONE_TOPIC = "service-one-topic";
    private static final String SERVICE_TWO_TOPIC = "service-two-topic";
    private static final String SERVICE_THREE_TOPIC = "service-three-topic";

    private static final BaggageField MESSAGEID = BaggageField.create("messageid");

    private final KafkaStreamsTracing kafkaStreamsTracing;
    private final Tracer tracer;

    @Bean
    public KStream<String, String> kStream(StreamsBuilder builder) {
        KStream<String, String> serviceOneStream = builder.stream(SERVICE_ONE_TOPIC);
        serviceOneStream
                // Append diagnostic information
                .transformValues(kafkaStreamsTracing.peek("service-one", (k, v) -> {
                    MESSAGEID.updateValue(tracer.currentSpan().context(), "messageid_" + v);
                    log.info("service-one messageid for {} -> {}", k, v);
                    log.info("MDC: {}", MDC.getCopyOfContextMap());
                }));
        serviceOneStream.mapValues((ValueMapper<String, String>) String::toUpperCase)
                .to(SERVICE_THREE_TOPIC);
        serviceOneStream.print(Printed.toSysOut());

        return serviceOneStream;
    }

}
