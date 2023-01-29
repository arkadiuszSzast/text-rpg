package com.szastarek.text.rpg

import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.wait.strategy.Wait

private const val MONGO_PORT = 27017

object MongoContainer {
    private val instance by lazy { startMongoContainer() }
    val host: String by lazy { instance.host }
    val port: Int by lazy { instance.getMappedPort(MONGO_PORT) }

    private fun startMongoContainer() = MongoDBContainer("mongo:5.0").apply {
        setWaitStrategy(Wait.forListeningPort())
        start()
    }
}
