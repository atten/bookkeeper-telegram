package bookkeeper.telegram;

public class App {

    public static void main(String[] args) {
        TelegramContainer container = DaggerTelegramContainer.builder().build();

        var bot = container.bot();

        bot.setup();
        Config.telegramUserIdToNotify().ifPresent(bot::notifyStartup);
        bot.listen();
    }
}
