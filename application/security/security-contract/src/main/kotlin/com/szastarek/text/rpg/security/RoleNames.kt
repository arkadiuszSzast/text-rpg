package com.szastarek.text.rpg.security

object RoleNames {
    val superUser = RoleName("SUPER_USER")
    val regularUser = RoleName("REGULAR_USER")

    fun find(name: String) = when (name) {
        superUser.value -> superUser
        regularUser.value -> regularUser
        else -> null
    }
}
