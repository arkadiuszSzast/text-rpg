package com.szastarek.text.rpg.shared.config

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.HoconApplicationConfig

private object ConfigMap {
    val config = HoconApplicationConfig(ConfigFactory.load())
}

fun getProperty(key: ConfigKey) = ConfigMap.config.property(key.key).getString()

fun getAsLong(key: ConfigKey) = ConfigMap.config.property(key.key).getString().toLong()

fun getPropertyAsBoolean(key: ConfigKey) = ConfigMap.config.property(key.key).getString().toBoolean()

fun getPropertyOrNull(key: ConfigKey) = ConfigMap.config.propertyOrNull(key.key)?.getString()

@JvmInline
value class ConfigKey(val key: String) {
    operator fun plus(other: ConfigKey) = ConfigKey("$key.${other.key}")
}
