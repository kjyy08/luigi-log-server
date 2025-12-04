rootProject.name = "blog-server"

include(":libs:common")
include(":libs:jpa")

include("domain:member")
include("domain:content")
include("domain:media")

include("app")
