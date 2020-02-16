package org.sjoblomj.shipmember

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import kotlin.properties.Delegates

@Component
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "application")
class ApplicationSettings {

  var emailUseSsl: Boolean by Delegates.notNull()
  lateinit var emailHost: String
  var emailPort: Int by Delegates.notNull()
  lateinit var emailUsername: String
  lateinit var emailPassword: String
  var delayBetweenEmails: Int by Delegates.notNull()
}
