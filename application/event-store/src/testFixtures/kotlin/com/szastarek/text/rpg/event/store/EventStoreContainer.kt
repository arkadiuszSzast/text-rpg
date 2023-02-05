package com.szastarek.text.rpg.event.store

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait

private const val EVENT_STORE_DB_PORT = 2113

object EventStoreContainer {
    private val instance by lazy { startEventStoreContainer() }
    val host: String by lazy { instance.host }
    val port: Int by lazy { instance.getMappedPort(EVENT_STORE_DB_PORT) }

    private fun startEventStoreContainer() = GenericContainer("eventstore/eventstore:20.10.2-buster-slim")
        .apply {
            addExposedPorts(EVENT_STORE_DB_PORT)
            addEnv("EVENTSTORE_CLUSTER_SIZE", "1")
            addEnv("EVENTSTORE_RUN_PROJECTIONS", "All")
            addEnv("EVENTSTORE_START_STANDARD_PROJECTIONS", "true")
            addEnv("EVENTSTORE_EXT_TCP_PORT", "1113")
            addEnv("EVENTSTORE_HTTP_PORT", "2113")
            addEnv("EVENTSTORE_INSECURE", "true")
            addEnv("EVENTSTORE_ENABLE_EXTERNAL_TCP", "true")
            addEnv("EVENTSTORE_ENABLE_ATOM_PUB_OVER_HTTP", "true")
            addEnv("EVENTSTORE_MEM_DB", "true")
            setWaitStrategy(Wait.forListeningPort())
            start()
        }
}
