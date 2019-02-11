package org.sjoblomj.shipmember.outputters

import jodd.mail.Email
import jodd.mail.MailServer
import org.sjoblomj.shipmember.dtos.EmailSettings

private const val emailSubject = "Inbjudan årsmöte"

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
      .subject(emailSubject)
      .htmlMessage(emailContent)

  val session = smtpServer.createSession()
  session.open()
  session.sendMail(email)
  session.close()
}
