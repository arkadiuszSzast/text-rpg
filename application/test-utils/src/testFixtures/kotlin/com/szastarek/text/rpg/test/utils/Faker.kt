package com.szastarek.text.rpg.test.utils

import io.github.serpro69.kfaker.Faker
import io.github.serpro69.kfaker.faker
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime.now
import java.time.LocalDateTime.ofEpochSecond
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import kotlin.random.Random

val faker by lazy { faker {} }

val Faker.date: FakerDate
    get() = FakerDate(this)

data class FakerDate(private val faker: Faker) {
    val localDateTime = FakerLocalDateTime(faker)
}

data class FakerLocalDateTime(private val faker: Faker) {
    fun future(atMost: Long, unit: TimeUnit): LocalDateTime {
        return future(atMost, unit, now(ZoneId.of("UTC")).toKotlinLocalDateTime())
    }

    fun future(atMost: Long, unit: TimeUnit, now: LocalDateTime): LocalDateTime {
        val minBound = now.toJavaLocalDateTime().plusSeconds(1).toInstant(ZoneOffset.UTC).epochSecond
        val maxBound =
            now.toJavaLocalDateTime().plusSeconds(unit.toSeconds(atMost)).toInstant(ZoneOffset.UTC).epochSecond

        val randomInEpochSeconds = Random.nextLong(minBound, maxBound)

        return ofEpochSecond(randomInEpochSeconds, 0, ZoneOffset.UTC).toKotlinLocalDateTime()
    }

    fun past(atMost: Long, unit: TimeUnit): LocalDateTime {
        return past(atMost, unit, now(ZoneId.of("UTC")).toKotlinLocalDateTime())
    }

    fun past(atMost: Long, unit: TimeUnit, now: LocalDateTime): LocalDateTime {
        val minBound =
            now.toJavaLocalDateTime().minusSeconds(unit.toSeconds(atMost)).toInstant(ZoneOffset.UTC).epochSecond
        val maxBound = now.toJavaLocalDateTime().minusSeconds(1).toInstant(ZoneOffset.UTC).epochSecond

        val randomInEpochSeconds = Random.nextLong(minBound, maxBound)

        return ofEpochSecond(randomInEpochSeconds, 0, ZoneOffset.UTC).toKotlinLocalDateTime()
    }
}
