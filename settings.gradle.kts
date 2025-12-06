rootProject.name = "blog-server"

include("app")
include("libs:common")
include("domain:member:domain")
include("domain:member:application")
include("domain:member:adapter:in:web")
include("domain:member:adapter:out:persistence:jpa")
include("domain:member:adapter:out:persistence:redis")
include("domain:member:adapter:out:auth:jwt")
include("domain:content")
include("domain:media")

include("libs:adapter:persistence:jpa")
include("libs:adapter:persistence:redis")
include("libs:adapter:message:spring")
include("libs:adapter:web")
