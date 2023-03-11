package com.szastarek.text.rpg.shared

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import java.lang.RuntimeException

fun <A> Option<A>.orThrow(throwable: () -> Throwable): A {
    when(this) {
        is Some -> return this.value
        is None -> throw throwable()
    }
}

data class OptionEmptyException(override val message: String) : RuntimeException(message)
