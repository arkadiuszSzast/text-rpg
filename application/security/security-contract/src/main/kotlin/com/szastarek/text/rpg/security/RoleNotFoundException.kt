package com.szastarek.text.rpg.security

import java.lang.RuntimeException

data class RoleNotFoundException(val roleName: RoleName) : RuntimeException("Role with name $roleName not found")
