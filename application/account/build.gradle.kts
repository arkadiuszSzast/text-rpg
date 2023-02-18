dependencies {
    api(project(":application:account:account-contract"))
    implementation(project(":application:shared"))
    implementation(project(":application:mediator"))
    implementation(project(":application:event-store"))
    implementation(project(":application:security"))

    testFixturesImplementation(testFixtures(project(":application:test-utils")))
    testFixturesImplementation(testFixtures(project(":application:mongo-db-access")))

    testImplementation(testFixtures(project(":application:mongo-db-access")))
    testImplementation(testFixtures(project(":application:event-store")))
    testImplementation(testFixtures(project(":application:test-utils")))
}
