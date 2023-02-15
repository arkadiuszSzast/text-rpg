package com.szastarek.text.rpg.security

import com.szastarek.acl.RegularRole
import com.szastarek.acl.Role
import com.szastarek.acl.SuperUserRole

class StaticRolesProvider : RoleProvider {

    private val roles = mapOf(
        RoleNames.superUser to SuperUserRole,
        RoleNames.regularUser to RegularRole("REGULAR_USER", listOf()),
    )
    override fun getByName(name: RoleName): Role {
        return roles[name] ?: throw RoleNotFoundException(name)
    }
}
