package bookkeeper.telegram;

public class App {

    public static void main(String[] args) {
        var bot = new Bot();

        var notify = Config.telegramUserIdToNotify();
        notify.ifPresent(bot::notifyStartup);

        bot.listen();
    }
}
