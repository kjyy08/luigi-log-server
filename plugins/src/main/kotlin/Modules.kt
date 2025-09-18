object Modules {
    object Libs {
        val commonDomain = Module(":libs:common-domain")
        val commonInfrastructure = Module(":libs:common-infrastructure")
        val commonWeb = Module(":libs:common-web")
    }

    object Service {
        object User {
            val core = Module(":service:user:core")
            val adapterIn = Module(":service:user:adapter-in")
            val adapterOut = Module(":service:user:adapter-out")
        }

        object Content {
            val core = Module(":service:content:core")
            val adapterIn = Module(":service:content:adapter-in")
            val adapterOut = Module(":service:content:adapter-out")
        }

        object Search {
            val core = Module(":service:search:core")
            val adapterIn = Module(":service:search:adapter-in")
            val adapterOut = Module(":service:search:adapter-out")
        }

        object Analytics {
            val core = Module(":service:analytics:core")
            val adapterIn = Module(":service:analytics:adapter-in")
            val adapterOut = Module(":service:analytics:adapter-out")
        }

        object Ai {
            val core = Module(":service:ai:core")
            val adapterIn = Module(":service:ai:adapter-in")
            val adapterOut = Module(":service:ai:adapter-out")
        }
    }

    object Mains {
        val monolithMain = Module(":mains:monolith-main")
    }
}

data class Module(val path: String)