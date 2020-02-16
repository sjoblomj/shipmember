package org.sjoblomj.shipmember

import mu.KotlinLogging
import org.sjoblomj.shipmember.dtos.Arguments
import org.sjoblomj.shipmember.dtos.EmailSettings
import org.sjoblomj.shipmember.dtos.MEMBERTYPES
import org.sjoblomj.shipmember.parsers.parseArgs
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ShipMemberApplication(private val applicationSettings: ApplicationSettings) : CommandLineRunner {

  private val log = KotlinLogging.logger {}

  override fun run(vararg args: String?) {
    val arguments = parseArgs(args.mapNotNull { it })
    val subject = readEmailSubject(arguments)
    val emailSettings = createEmailSettings(subject)

    notifyMembers(emailSettings, arguments)
  }

  private fun readEmailSubject(arguments: Arguments): String {
    val subject = when {
      arguments.emailSubject  != "" -> arguments.emailSubject
      arguments.wantedMembers != MEMBERTYPES.WITHOUT_EMAILS -> {
        log.info("Please give a subject for the emails:")
        return readLine()!!
      }
      else -> ""
    }
    log.info("Will use this as Email subject: '$subject'")
    return subject
  }

  private fun createEmailSettings(subject: String) = EmailSettings(
        applicationSettings.emailUseSsl,
        applicationSettings.emailHost,
        applicationSettings.emailPort,
        applicationSettings.emailUsername,
        applicationSettings.emailPassword,
        subject,
        applicationSettings.delayBetweenEmails
  )

  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      runApplication<ShipMemberApplication>(*args)
    }
  }
}
