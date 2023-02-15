val codified_version: String by project

dependencies {
    api(project(":application:security:security-contract"))
    implementation(project(":application:shared"))
    implementation(project(":application:account:account-contract"))

    testFixturesImplementation("com.github.bright.codified:enums:$codified_version")
    testFixturesImplementation(testFixtures(project(":application:test-utils")))
}
