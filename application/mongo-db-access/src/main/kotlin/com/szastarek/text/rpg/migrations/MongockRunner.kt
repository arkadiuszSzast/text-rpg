package com.szastarek.text.rpg.migrations

import com.mongodb.reactivestreams.client.MongoClient
import com.szastarek.text.rpg.config.DatabaseConfig
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver
import io.mongock.runner.standalone.MongockStandalone
import mu.KotlinLogging

class MongockRunnerWrapper(private val mongoClient: MongoClient) {
    private val log = KotlinLogging.logger {}

    private val mongockRunner = MongockStandalone.builder()
        .setDriver(
            MongoReactiveDriver.withDefaultLock(
                mongoClient,
                DatabaseConfig.name
            )
        )
        .addMigrationScanPackage("com.szastarek.text.rpg.migrations")
        .addDependency(mongoClient)
        .setMigrationStartedListener { log.info { "Executing migration" } }
        .setMigrationStartedListener { log.info { "Finished executing migration" } }
        .setMigrationFailureListener {
            log.error { "Failed to execute migration because of: ${it.exception.message}" }
            throw it.exception
        }
        .buildRunner()
        .also { it.execute() }


}

