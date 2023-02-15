package com.szastarek.text.rpg.security

import com.szastarek.acl.Role

interface RoleProvider {
    fun getByName(name: RoleName): Role
}
