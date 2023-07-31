package bookkeeper.telegram;

public class App {
    public static void main(String[] args) {
        var bot = new Bot(System.getenv("BOT_TOKEN"));
        bot.listen();
    }
}
