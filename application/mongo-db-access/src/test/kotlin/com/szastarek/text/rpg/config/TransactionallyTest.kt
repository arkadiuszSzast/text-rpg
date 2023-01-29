package com.szastarek.text.rpg.config

import com.szastarek.text.rpg.DatabaseTest
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.eq
import org.litote.kmongo.newId
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isEmpty

class TransactionallyTest : DatabaseTest(emptyList()) {
    init {
        describe("rollback transaction") {

            it("should rollback insert when exception occurred") {
                // arrange && act
                runCatching {
                    transactionally.call { session ->
                        db.getCollection<Person>().insertOne(session, Person(newId(), "Joe"))
                        throw Error()
                        @Suppress("UNREACHABLE_CODE")
                        db.getCollection<Person>().insertOne(session, Person(newId(), "Doe"))
                    }
                }

                // assert
                expectThat(db.getCollection<Person>().find().toList())
                    .isEmpty()
            }

            it("should rollback delete when exception occurred") {
                // arrange
                db.getCollection<Person>().insertOne(Person(newId(), "Joe"))

                // act
                runCatching {
                    transactionally.call { session ->
                        db.getCollection<Person>().deleteOne(session, Person::name eq "Joe")
                        throw Error()
                    }
                }

                // assert
                expectThat(db.getCollection<Person>().find().toList())
                    .hasSize(1)
            }
        }

        describe("without transaction") {

            it("should save everything before exception") {
                // arrange && act
                runCatching {
                    db.getCollection<Person>().insertOne(Person(newId(), "Joe"))
                    throw Error()
                    @Suppress("UNREACHABLE_CODE")
                    db.getCollection<Person>().insertOne(Person(newId(), "Doe"))
                }

                // assert
                expectThat(db.getCollection<Person>().find().toList())
                    .hasSize(1)
            }
        }
    }
}

@Serializable
data class Person(@Contextual val id: Id<Person>, val name: String)
