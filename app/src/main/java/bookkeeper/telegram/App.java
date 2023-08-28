package bookkeeper.telegram;

public class App {

    public static void main(String[] args) {
        var bot = new Bot();

        var notify = Config.notifyTelegramUserId();
        if (notify != null) {
            bot.notifyStartup(notify);
        }

        bot.listen();
    }
}
