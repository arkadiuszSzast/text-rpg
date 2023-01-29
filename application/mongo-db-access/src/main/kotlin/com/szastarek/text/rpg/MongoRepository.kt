package com.szastarek.text.rpg

import com.mongodb.client.result.UpdateResult
import org.litote.kmongo.Id
import org.litote.kmongo.coroutine.CoroutineCollection

interface MongoRepository<T : HasId<T>> {
    val collection: CoroutineCollection<T>

    suspend fun findById(id: Id<T>): T? {
        return collection.findOneById(id)
    }

    suspend fun updateById(id: Id<T>, value: T): UpdateResult {
        return collection.updateOneById(id, value)
    }

    suspend fun save(entity: T): UpdateResult? {
        return collection.save(entity)
    }
}
