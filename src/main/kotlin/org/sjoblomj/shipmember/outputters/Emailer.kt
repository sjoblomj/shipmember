package org.sjoblomj.shipmember.outputters

import jodd.mail.Email
import jodd.mail.MailServer
import org.sjoblomj.shipmember.dtos.EmailSettings

fun sendEmail(emailSettings: EmailSettings, receiverEmailAddress: String, emailContent: String) {
  val smtpServer = MailServer.create()
      .ssl(emailSettings.useSsl)
      .host(emailSettings.host)
      .port(emailSettings.port)
      .auth(emailSettings.username, emailSettings.password)
      .buildSmtpMailServer()

  val email = Email.create()
      .from(emailSettings.username)
      .to(receiverEmailAddress)
      .subject(emailSettings.subject)
      .htmlMessage(emailContent)

  smtpServer.createSession().use {
    it.open()
    it.sendMail(email)
  }
}
