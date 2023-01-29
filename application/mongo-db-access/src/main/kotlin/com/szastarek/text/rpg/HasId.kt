package com.szastarek.text.rpg

import org.litote.kmongo.Id

interface HasId<T> {
    val id: Id<T>
}
