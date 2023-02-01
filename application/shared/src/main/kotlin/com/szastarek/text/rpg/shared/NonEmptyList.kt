package com.szastarek.text.rpg.shared

import arrow.core.NonEmptyList
import arrow.typeclasses.Semigroup

fun <A> NonEmptyList<A>.reduce(semigroup: Semigroup<A>) = semigroup.run {
    this@reduce.reduce { acc, next ->
        acc.combine(next)
    }
}
