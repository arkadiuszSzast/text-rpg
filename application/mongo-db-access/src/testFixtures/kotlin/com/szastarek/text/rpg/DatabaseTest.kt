package com.szastarek.text.rpg

import com.szastarek.text.rpg.transactionally.Transactionally
import com.szastarek.text.rpg.test.utils.serializationModule
import io.kotest.common.runBlocking
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.test.TestCase
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.litote.kmongo.coroutine.CoroutineDatabase

abstract class DatabaseTest(private val neededModules: List<Module>) : KoinTest, HasDatabaseAndTransactionally,
    DescribeSpec() {
    constructor(module: Module) : this(listOf(module))

    override val transactionally: Transactionally by inject()
    override val db: CoroutineDatabase by inject()

    override suspend fun beforeEach(testCase: TestCase) {
        runBlocking {
            db.dropAllCollections()
        }
    }

    override suspend fun afterSpec(spec: Spec) {
        stopKoin()
    }

    init {
        startKoin { modules(neededModules.toList().plus(kmongoTestingModule).plus(serializationModule)) }
    }
}

suspend fun CoroutineDatabase.dropAllCollections() = listCollectionNames().forEach { dropCollection(it) }
