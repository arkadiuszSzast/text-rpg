package com.szastarek.text.rpg.event.store

import com.trendyol.kediatr.CommandMetadata

fun EventMetadata.toCommandMetadata() =
    CommandMetadata(this.correlationId?.correlationId, this.causationId?.causationId)
