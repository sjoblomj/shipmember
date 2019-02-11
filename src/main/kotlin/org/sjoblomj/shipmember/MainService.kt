package org.sjoblomj.shipmember

import mu.KotlinLogging
import org.sjoblomj.shipmember.dtos.*
import org.sjoblomj.shipmember.outputters.createLatexFile
import org.sjoblomj.shipmember.outputters.renderHtml
import org.sjoblomj.shipmember.outputters.sendEmail
import org.sjoblomj.shipmember.parsers.parseFile

private val log = KotlinLogging.logger {}

fun notifyMembers(emailSettings: EmailSettings, arguments: Arguments) {
  var households = parseFile(arguments.inputFile)
  if (arguments.onlyNonPayers)
    households = households.filter { !it.hasPaid() }

  if (arguments.householdNumbers.isNotEmpty())
    households = households.filter { arguments.householdNumbers.contains(it.getHouseholdNumber()) }

  if (arguments.wantedMembers == MEMBERTYPES.WITHOUT_EMAILS)
    households = households.filter { it.getFirstEmail() == "" }
  if (arguments.wantedMembers == MEMBERTYPES.WITH_EMAILS)
    households = households.filter { it.getFirstEmail() != "" }


  val emailRecipients = getEmailRecipients(households, arguments)
  val letterRecipients = getLetterRecipients(households, arguments)

  emailRecipients.forEach { sendEmail(emailSettings, it) }
  letterRecipients.forEach { createLatexFile(it, arguments.outputDirectory) }
}

private fun getEmailRecipients(households: List<Household>, arguments: Arguments): List<Household> {
  return households.filter { it.getFirstEmail() != "" && arguments.outputType != OUTPUTTYPES.PDF_ONLY }
}

private fun getLetterRecipients(households: List<Household>, arguments: Arguments): List<Household> {
  return households.filter { it.getFirstEmail() == "" || arguments.outputType != OUTPUTTYPES.EMAIL_OVER_PDF }
}

private fun sendEmail(emailSettings: EmailSettings, household: Household) {
  val firstNames = household.getAllFirstNames().joinToString(", ")
  val surnames = household.getAllSurnames().joinToString(", ")
  val recipientName = if (household.hasSeveralMembers()) "$surnames ($firstNames)" else "$firstNames $surnames"

  val emailContent = renderHtml(household)
  log.debug { "Sending email to $recipientName" }
  sendEmail(emailSettings, household.getFirstEmail(), emailContent)
}
