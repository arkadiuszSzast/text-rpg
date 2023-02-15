package com.szastarek.text.rpg.account.query

import arrow.core.Option
import com.szastarek.acl.AccountContext
import com.szastarek.text.rpg.account.Account
import com.trendyol.kediatr.Query
import org.litote.kmongo.Id

data class FindAccountContextByIdQuery(val id: Id<Account>) : Query<Option<AccountContext>>
