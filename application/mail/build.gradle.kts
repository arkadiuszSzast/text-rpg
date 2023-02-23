val sendgrid_version: String by project

dependencies {
    api(project(":application:mail:mail-contract"))
    implementation(project(":application:shared"))
    implementation(project(":application:event-store"))
    implementation(project(":application:security"))

    implementation("com.sendgrid:sendgrid-java:$sendgrid_version")

    testFixturesImplementation(testFixtures(project(":application:test-utils")))

    testImplementation(testFixtures(project(":application:test-utils")))
    testImplementation(testFixtures(project(":application:event-store")))
}
