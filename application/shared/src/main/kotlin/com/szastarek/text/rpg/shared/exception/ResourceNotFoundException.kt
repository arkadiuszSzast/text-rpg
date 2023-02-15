package com.szastarek.text.rpg.shared.exception

import java.lang.RuntimeException
import kotlin.reflect.KClass
import org.litote.kmongo.Id

data class ResourceNotFoundException(val id: Id<*>, val type: KClass<*>) :
    RuntimeException("Resource of type [${type.simpleName}] with id [$id] not found")

inline fun <reified T> resourceNotFoundException(id: Id<T>) = ResourceNotFoundException(id, T::class)
