package org.sjoblomj.shipmember.outputters

import com.github.sleroy.fakesmtp.core.ServerConfiguration
import com.github.sleroy.junit.mail.server.test.FakeSmtpRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.sjoblomj.shipmember.dtos.EmailSettings
import kotlin.test.assertTrue

class EmailerTests {

  private val smtpPort = 2525

  @get:Rule
  val smtpServer = FakeSmtpRule(ServerConfiguration.create()
      .port(smtpPort)
      .relayDomains("bepa.com")
      .charset("UTF-8"))

  private lateinit var emailSettings: EmailSettings

  @Before fun setup() {
    emailSettings = EmailSettings(false, "localhost", smtpPort, "apa", "bepa", "subject")

    assertTrue(smtpServer.isRunning)
  }


  @Test fun `Can send email`() {
    assertTrue(smtpServer.mailBox().isEmpty())

    sendEmail(emailSettings, "apa@bepa.com", "This <b>HTML</b> message is a test...")

    assertTrue(smtpServer.mailBox().isNotEmpty())
  }
}
