package com.szastarek.text.rpg.shared

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import java.lang.RuntimeException
import kotlin.reflect.KClass
import org.litote.kmongo.Id

fun <A> Option<A>.orThrow(throwable: () -> Throwable): A {
    when(this) {
        is Some -> return this.value
        is None -> throw throwable()
    }
}

data class ResourceNotFoundException(val clazz: KClass<*>, val id: Id<*>)
    : RuntimeException("Resource of type ${clazz.simpleName} with id $id not found")

inline fun <reified T> resourceNotFoundException(id: Id<T>): ResourceNotFoundException =
    ResourceNotFoundException(T::class, id)
