
package com.fetch.receiptprocessor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fetch.receiptprocessor.util.LocalDateDeserializer;
import com.fetch.receiptprocessor.util.LocalDateSerializer;
import com.fetch.receiptprocessor.util.LocalTimeDeserializer;
import com.fetch.receiptprocessor.util.LocalTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        // Register custom serializers and deserializers for LocalDate and LocalTime
        module.addSerializer(LocalDate.class, new LocalDateSerializer());
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        module.addSerializer(LocalTime.class, new LocalTimeSerializer());
        module.addDeserializer(LocalTime.class, new LocalTimeDeserializer());

        mapper.registerModule(module);

        // Register JavaTimeModule to handle LocalDate and LocalTime
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
