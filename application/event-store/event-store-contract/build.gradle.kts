val event_store_db_version: String by project

dependencies {
    implementation(project(":application:mediator"))
    implementation(project(":application:shared"))
    implementation("com.github.arkadiuszSzast:ktor-event-store-db:$event_store_db_version")
}
