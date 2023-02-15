package com.szastarek.text.rpg.security

import java.lang.RuntimeException

class NotAuthenticatedException : RuntimeException("User is not authenticated")
