val koin_version: String by project
val kmongo_version: String by project
val kotest_version: String by project
val test_containers_version: String by project
val mongock_version: String by project

dependencies {
    api("org.litote.kmongo:kmongo-coroutine-serialization:$kmongo_version")
    api("org.litote.kmongo:kmongo-id-serialization:$kmongo_version")

    implementation(project(":application:shared"))
    implementation("io.mongock:mongock-standalone:$mongock_version")
    implementation("io.mongock:mongodb-reactive-driver:$mongock_version")

    testFixturesImplementation("org.testcontainers:mongodb:$test_containers_version")
    testFixturesImplementation("io.insert-koin:koin-test:$koin_version")
    testFixturesImplementation("io.kotest:kotest-runner-junit5:$kotest_version")
    testFixturesImplementation(testFixtures(project(":application:test-utils")))
}
