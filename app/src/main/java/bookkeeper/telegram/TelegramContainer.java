package bookkeeper.telegram;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(
    modules = {
        Config.class,
        AllHandlersModule.class,
    }
)
interface TelegramContainer {
    Bot bot();
}
