package com.szastarek.text.rpg.security

import com.szastarek.acl.authority.Decision
import com.szastarek.acl.authority.Deny

inline fun Decision.onDenied(onDenied: () -> Unit) {
    if (this is Deny) {
        onDenied()
    }
}