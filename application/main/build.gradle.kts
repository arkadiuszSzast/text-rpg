val ktor_version: String by project

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

plugins {
    application
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")

    implementation(project(":application:shared"))
    implementation(project(":application:mongo-db-access"))
    implementation(project(":application:mediator"))
}
