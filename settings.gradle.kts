rootProject.name = "luigi-log-server"

// Common Libraries
include("libs:common-domain")
include("libs:common-infrastructure")  
include("libs:common-web")

// Main Application
include("mains:monolith-main")

// Service Modules - Phase 1
include("service:user:core")
include("service:user:adapter-in")
include("service:user:adapter-out")

include("service:content:core")
include("service:content:adapter-in")
include("service:content:adapter-out")

include("service:search:core")
include("service:search:adapter-in")
include("service:search:adapter-out")

include("service:analytics:core")
include("service:analytics:adapter-in")
include("service:analytics:adapter-out")

// Service Modules - Phase 2 (AI)
include("service:ai:core")
include("service:ai:adapter-in")
include("service:ai:adapter-out")
