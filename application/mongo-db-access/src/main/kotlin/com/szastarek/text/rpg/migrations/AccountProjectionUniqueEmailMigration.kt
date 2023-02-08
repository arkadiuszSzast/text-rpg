package com.szastarek.text.rpg.migrations

import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import com.mongodb.reactivestreams.client.MongoClient
import com.szastarek.text.rpg.config.DatabaseConfig
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.runBlocking

@ChangeUnit(id = "account_projection_email_unique", order = "001", author = "szastarek", systemVersion = "1.0.0")
class AccountProjectionUniqueEmailMigration {
    @Execution
    fun migrationMethod(mongoClient: MongoClient) {
        runBlocking {
            mongoClient.getDatabase(DatabaseConfig.name).getCollection("accountProjection")
                .createIndex(Indexes.ascending("emailAddress"), IndexOptions().unique(true).name("emailAddressUnique"))
                .awaitLast()
        }
    }

    @RollbackExecution
    fun rollback(mongoClient: MongoClient) {
        runBlocking {
            mongoClient.getDatabase(DatabaseConfig.name).getCollection("accountProjection")
                .dropIndex("emailAddressUnique")
                .awaitLast()
        }
    }
}