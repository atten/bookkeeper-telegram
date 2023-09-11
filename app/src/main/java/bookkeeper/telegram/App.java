package bookkeeper.telegram;

public class App {

    public static void main(String[] args) {
        TelegramContainer container = DaggerTelegramContainer.builder().build();

        var bot = container.bot();

        var notify = Config.telegramUserIdToNotify();
        notify.ifPresent(bot::notifyStartup);

        bot.listen();
    }
}
