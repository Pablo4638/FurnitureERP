package group2.projecte2.config;

import group2.projecte2.serveis.MessageService;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoMessageSourceConfig {

    @Bean
    public MessageSource mongoMessageSource(MessageService messageService) {
        return new MongoMessageSource(messageService);
    }
}