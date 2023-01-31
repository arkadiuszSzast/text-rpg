val kediatr_version: String by project

dependencies {
    api("com.github.arkadiuszSzast:kediatR-koin-starter:$kediatr_version")

    implementation(project(":application:shared"))

    testImplementation(testFixtures(project(":application:test-utils")))
}
