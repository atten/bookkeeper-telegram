package bookkeeper.enums;

public enum HandlerPriority {
    // Structured text input, /commands and callbacks: Default choice.
    NORMAL_COMMAND,
    // Uncategorized text input with severity below normal.
    LOW_MESSAGE,
    // Uncategorized text input with severity under below (in case it clashes with handlers with LOW).
    LOWEST_MESSAGE
}
