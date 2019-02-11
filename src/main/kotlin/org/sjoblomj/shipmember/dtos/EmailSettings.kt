package org.sjoblomj.shipmember.dtos

data class EmailSettings(val useSsl: Boolean, val host: String, val port: Int,
                         val username: String, val password: String)
