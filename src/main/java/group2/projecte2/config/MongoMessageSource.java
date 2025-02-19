package group2.projecte2.config;

import group2.projecte2.serveis.MessageService;
import org.springframework.context.support.AbstractMessageSource;

import java.text.MessageFormat;
import java.util.Locale;

public class MongoMessageSource extends AbstractMessageSource {

    private final MessageService messageService;

    public MongoMessageSource(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        String message = messageService.getMessage(code, locale.getLanguage());
        if (message == null) {
            return new MessageFormat(code, locale);
        }
        return new MessageFormat(message, locale);
    }
}
