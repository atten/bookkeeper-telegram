package bookkeeper.telegram;

public class App {

    public static void main(String[] args) {
        var config = new Config();
        var bot = new Bot(config);
        bot.listen();
    }
}
