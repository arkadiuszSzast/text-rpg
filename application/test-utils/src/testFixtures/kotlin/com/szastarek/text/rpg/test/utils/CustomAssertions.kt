package com.szastarek.text.rpg.test.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import strikt.api.Assertion

fun Assertion.Builder<Instant>.isUpToOneSecondOld() {
    assert("Instant is equal to now ignoring seconds") {
        val epochSecondsNow = Clock.System.now().epochSeconds
        val epochSecondsMinusOne = epochSecondsNow - 1
        val givenEpochSeconds = it.epochSeconds

        if (givenEpochSeconds >= epochSecondsMinusOne || givenEpochSeconds <= epochSecondsNow) {
            pass()
        } else {
            fail("Given Instant is equal is not between $epochSecondsMinusOne and $epochSecondsNow")
        }
    }
}
