package com.szastarek.text.rpg.account.response

import com.szastarek.text.rpg.shared.jwt.JwtToken
import kotlinx.serialization.Serializable

@Serializable
data class LoginAccountResponse(val token: JwtToken)
