package group2.projecte2.config;

import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import group2.projecte2.serveis.implementacio.TelegramServei;

@Configuration
public class NaturaBotConfig {

    public NaturaBotConfig(TelegramServei handleTesting) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(handleTesting);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}