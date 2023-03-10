val ktor_version: String by project
val kmongo_version: String by project
val strikt_version: String by project
val faker_version: String by project

dependencies {
    testFixturesApi("io.github.serpro69:kotlin-faker:$faker_version")

    testFixturesImplementation(project(":application:shared"))
    testFixturesImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testFixturesImplementation("io.strikt:strikt-core:$strikt_version")
    testFixturesImplementation("io.ktor:ktor-serialization:$ktor_version")
    testFixturesImplementation("org.litote.kmongo:kmongo-id-serialization:$kmongo_version")
}
