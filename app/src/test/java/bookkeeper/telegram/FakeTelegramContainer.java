package bookkeeper.telegram;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
    FakeConfig.class,
    AllHandlersModule.class,
})
interface FakeTelegramContainer {
    Bot bot();
    FakeTelegramBot fakeTelegramBot();
}
