package com.szastarek.text.rpg.shared.response

import com.szastarek.text.rpg.shared.isBetween

private const val MIN_SUCCESS_RESPONSE_CODE = 200
private const val MAX_SUCCESS_RESPONSE_CODE = 299
@JvmInline
value class ResponseCode(val code: Int) {
    val isSuccess
        get() = code.isBetween(MIN_SUCCESS_RESPONSE_CODE, MAX_SUCCESS_RESPONSE_CODE)
}