plugins {
    id("org.jetbrains.kotlinx.kover")
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    "*.*Application*",
                    "*.config.*",
                    "*.dto.*",
                    "*.entity.*"
                )
            }
        }

        verify {
            rule {
                minBound(60)
            }
        }
    }
}
