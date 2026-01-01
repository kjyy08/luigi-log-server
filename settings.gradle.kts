rootProject.name = "blog-server"

include("app")
include("libs:common")
include("modules:member:domain")
include("modules:member:application")
include("modules:member:adapter:in:web")
include("modules:member:adapter:out:persistence:jpa")

include("modules:auth:token:domain")
include("modules:auth:token:application")
include("modules:auth:token:adapter:in:web")
include("modules:auth:token:adapter:in:event")
include("modules:auth:token:adapter:out:persistence:redis")
include("modules:auth:token:adapter:out:token:jwt")

include("modules:auth:credentials:domain")
include("modules:auth:credentials:application")
include("modules:auth:credentials:adapter:in:web")
include("modules:auth:credentials:adapter:in:event")
include("modules:auth:credentials:adapter:out:persistence:jpa")
include("modules:auth:credentials:adapter:out:client:member")

include("modules:content:domain")
include("modules:content:application")
include("modules:content:adapter:in:web")
include("modules:content:adapter:out:persistence:jpa")

include("libs:adapter:persistence:jpa")
include("libs:adapter:persistence:redis")
include("libs:adapter:message:spring")
include("libs:adapter:web")
