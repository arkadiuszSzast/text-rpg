val ktor_version: String by project
val kotlin_version: String by project
val koin_version: String by project
val logback_version: String by project
val kotlin_acl_version: String by project
val kmongo_version: String by project
val kotest_version: String by project
val strikt_version: String by project
val kotlin_datetime_version: String by project
val kotlin_logging_version: String by project
val logstash_logback_encoder_version: String by project
val codified_version: String by project
val arrow_version: String by project
val konform_version: String by project
val jbcrypt_version: String by project
val auth0_jwt_version: String by project
val event_store_db_version: String by project
val kediatr_version: String by project
val mongock_version: String by project

plugins {
    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.0"
}

allprojects {
    group = "com.szastarek"
    version = "0.0.1"
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven("https://maven.pkg.github.com/arkadiuszSzast/kotlin-acl-kotlinx-serializer") {
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
        maven("https://maven.pkg.github.com/arkadiuszSzast/ktor-event-store-db") {
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    apply(plugin = "kotlin")
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "org.gradle.java-test-fixtures")

    dependencies {
        implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
        implementation("io.ktor:ktor-server-auth:$ktor_version")
        implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")
        implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
        implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
        implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
        implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
        implementation("ch.qos.logback:logback-classic:$logback_version")
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlin_datetime_version")
        implementation("io.github.microutils:kotlin-logging-jvm:$kotlin_logging_version")
        implementation("io.insert-koin:koin-ktor:$koin_version")
        implementation("com.github.bright.codified:enums:$codified_version")
        implementation("com.github.bright.codified:enums-serializer:$codified_version")
        implementation("com.szastarek:kotlin-acl-kotlinx-serialization:${kotlin_acl_version}")
        implementation("org.litote.kmongo:kmongo-coroutine-serialization:${kmongo_version}")
        implementation("io.arrow-kt:arrow-core")
        implementation("io.konform:konform-jvm:$konform_version")
        implementation("org.mindrot:jbcrypt:$jbcrypt_version")
        implementation("io.ktor:ktor-server-status-pages:$ktor_version")
        implementation("com.auth0:java-jwt:$auth0_jwt_version")
        implementation("com.szastarek:kotlin-event-store-db:$event_store_db_version")
        implementation("com.github.arkadiuszSzast:kediatR-koin-starter:$kediatr_version")
        implementation("org.litote.kmongo:kmongo-coroutine-serialization:$kmongo_version")
        implementation("org.litote.kmongo:kmongo-id-serialization:$kmongo_version")
        implementation("io.mongock:mongock-standalone:$mongock_version")
        implementation("io.mongock:mongodb-reactive-driver:$mongock_version")

        testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
        testImplementation("io.kotest:kotest-runner-junit5:$kotest_version")
        testImplementation("io.strikt:strikt-core:$strikt_version")
        testImplementation("io.strikt:strikt-arrow:$strikt_version")
        testImplementation("io.insert-koin:koin-test:$koin_version")
        testImplementation(testFixtures("com.szastarek:kotlin-acl-kotlinx-serialization:${kotlin_acl_version}"))

        implementation(platform("io.arrow-kt:arrow-stack:$arrow_version"))
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    kotlin.target.compilations["test"].associateWith(kotlin.target.compilations["main"])
    kotlin.target.compilations["testFixtures"].associateWith(kotlin.target.compilations["main"])
}

