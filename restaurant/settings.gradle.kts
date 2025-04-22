rootProject.name = "restaurant"

include(
    // Common
    ":domains:common",

    // User
    ":domains:user:domain",
    ":domains:user:application",
    ":domains:user:infrastructure",
    ":domains:user:presentation",
    ":domains:user:apps",

    // Independent modules
    ":independent:outbox:api",
    ":independent:outbox:application",
    ":independent:outbox:infrastructure",
)

