package com.szastarek.text.rpg.account

import com.szastarek.text.rpg.shared.EmailAddress
import org.litote.kmongo.Id

interface Account {
    val id: Id<Account>
    val emailAddress: EmailAddress
}
