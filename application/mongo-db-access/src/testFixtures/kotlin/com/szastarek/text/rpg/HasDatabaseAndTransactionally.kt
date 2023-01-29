package com.szastarek.text.rpg

import com.szastarek.text.rpg.transactionally.Transactionally
import org.litote.kmongo.coroutine.CoroutineDatabase

interface HasDatabaseAndTransactionally {
    val transactionally: Transactionally
    val db: CoroutineDatabase
}
