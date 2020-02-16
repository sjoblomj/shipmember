package org.sjoblomj.shipmember

import org.sjoblomj.shipmember.dtos.EmailSettings
import org.sjoblomj.shipmember.parsers.parseArgs
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ShipMemberApplication(private val applicationSettings: ApplicationSettings) : CommandLineRunner {

  override fun run(vararg args: String?) {
    val arguments = parseArgs(args.mapNotNull { it })
    val emailSettings = createEmailSettings()

    notifyMembers(emailSettings, arguments)
  }

  private fun createEmailSettings(): EmailSettings = EmailSettings(
        applicationSettings.emailUseSsl,
        applicationSettings.emailHost,
        applicationSettings.emailPort,
        applicationSettings.emailUsername,
        applicationSettings.emailPassword,
        applicationSettings.delayBetweenEmails
  )

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      runApplication<ShipMemberApplication>(*args)
    }
  }
}
