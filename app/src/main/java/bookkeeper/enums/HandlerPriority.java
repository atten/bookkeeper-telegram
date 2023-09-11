package bookkeeper.enums;

public enum HandlerPriority {
    // Logging: Must come first.
    HIGHEST_LOGGING,
    // Context configuration: Right after logging.
    HIGH_CONFIGURATION,
    // Structured text input, /commands and callbacks: Default choice.
    NORMAL_COMMAND,
    // Uncategorized text input with severity below normal.
    LOW_MESSAGE,
    // Handlers which do not fit into categories above.
    LOWEST_FINALIZE
}
