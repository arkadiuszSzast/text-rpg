val test_containers_version: String by project
val koin_version: String by project
val kotest_version: String by project
val event_store_db_version: String by project

dependencies {
    api(project(":application:event-store:event-store-contract"))
    implementation(project(":application:mediator"))
    implementation(project(":application:shared"))

    testImplementation(testFixtures(project(":application:test-utils")))

    testFixturesApi(testFixtures("com.szastarek:kotlin-event-store-db:$event_store_db_version"))
    testFixturesImplementation("org.testcontainers:testcontainers:$test_containers_version")
    testFixturesImplementation("io.insert-koin:koin-test:$koin_version")
    testFixturesImplementation("io.kotest:kotest-runner-junit5:$kotest_version")
    testFixturesImplementation(testFixtures(project(":application:test-utils")))
}
