package bookkeeper.telegram;

class FakeApp {
    static final FakeTelegramContainer container = DaggerFakeTelegramContainer.builder().build();
}
