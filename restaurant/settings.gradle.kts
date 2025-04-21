rootProject.name = "restaurant"

include(
    // Common
    ":domains:common",

    // Account
    ":domains:account:domain",
    ":domains:account:application",
    ":domains:account:infrastructure",
    ":domains:account:presentation",
    ":domains:account:apps",

    // User
    ":domains:user:domain",
    ":domains:user:application",
    ":domains:user:infrastructure",
    ":domains:user:presentation",
    ":domains:user:apps",

    // Independent modules
    ":independent:outbox:application",
    ":independent:outbox:infrastructure",

    // Test
    ":test"
)

// include(":domains:restaurant:application")
// // include(":domains:restaurant:apps")
// include(":domains:restaurant:domain")
// include(":domains:restaurant:infrastructure")
// include(":domains:restaurant:presentation")

// include("libs:outbox")
// include("support:common")
