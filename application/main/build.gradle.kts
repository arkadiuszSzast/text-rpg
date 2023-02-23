application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

plugins {
    application
}

dependencies {
    implementation(project(":application:shared"))
    implementation(project(":application:mongo-db-access"))
    implementation(project(":application:mediator"))
    implementation(project(":application:event-store"))
    implementation(project(":application:account"))
    implementation(project(":application:security"))
    implementation(project(":application:mail"))
}
