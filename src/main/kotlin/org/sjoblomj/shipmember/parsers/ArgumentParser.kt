package org.sjoblomj.shipmember.parsers

import mu.KotlinLogging
import org.sjoblomj.shipmember.dtos.Arguments
import org.sjoblomj.shipmember.dtos.MEMBERTYPES
import org.sjoblomj.shipmember.dtos.OUTPUTTYPES
import java.io.File
import kotlin.system.exitProcess

private val log = KotlinLogging.logger {}

fun parseArgs(args : List<String>): Arguments {

  if (args.size == 1 && (args[0].toLowerCase() == "--help" || args[0].toLowerCase() == "-h")) {
    printHelpMessage()
    exitProcess(0)
  }

  var inputFile: String? = null
  var outputDir: String? = null
  var emailSubject = ""
  var onlyNonPayers = false
  var householdNumbers: List<Int> = emptyList()

  var memberTypes = MEMBERTYPES.ALL
  var outputTypes = OUTPUTTYPES.PDF_AND_EMAIL

  var index = -1
  for (i in args.indices) {
    index += 1

    if (index >= args.size) {
      break
    }

    when (args[index].toLowerCase()) {
      "--input" -> {
        inputFile = tryParsingInput(args, index + 1)
        index += 1
      }
      "--output" -> {
        outputDir = tryParsingOutput(args, index + 1)
        index += 1
      }
      "--email-subject" -> {
        val pair = tryParsingEmailSubject(args, index + 1)
        emailSubject = pair.first
        index += pair.second
      }
      "--household-numbers" -> {
        householdNumbers = tryParsingHouseholdNumbers(args, index + 1)
        index += 1
      }
      "--only-non-payers" -> {
        onlyNonPayers = true
      }
      "--parse-all" -> {
        memberTypes = MEMBERTYPES.ALL
      }
      "--parse-those-with-emails" -> {
        memberTypes = MEMBERTYPES.WITH_EMAILS
      }
      "--parse-those-without-emails" -> {
        memberTypes = MEMBERTYPES.WITHOUT_EMAILS
      }
      "--output-pdf-and-send-email" -> {
        outputTypes = OUTPUTTYPES.PDF_AND_EMAIL
      }
      "--output-pdf-only" -> {
        outputTypes = OUTPUTTYPES.PDF_ONLY
      }
      "--only-send-email-where-possible" -> {
        outputTypes = OUTPUTTYPES.EMAIL_OVER_PDF
      }
      else -> {
        printHelpMessage()
        throw IllegalArgumentException("Can't recognize argument '${args[index]}'")
      }
    }
  }

  if (inputFile == null || outputDir == null) {
    printHelpMessage()
    throw IllegalArgumentException("You need to specify an input file ('--input <file>') and output folder ('--output <folder>')")
  }
  return Arguments(inputFile, outputDir, emailSubject, onlyNonPayers, memberTypes, outputTypes, householdNumbers)
}

private fun tryParsingInput(args: List<String>, index: Int): String {
  if (noMoreArguments(args, index) || argumentStartsWithDashes(args, index)) {
    throw IllegalArgumentException("The argument following '--input' must be a valid file name")
  }

  if (!File(args[index]).exists()) {
    throw IllegalArgumentException("The argument following '--input' ('${args[index]}') does not point to a file")
  }

  return args[index]
}

private fun tryParsingOutput(args: List<String>, index: Int): String {
  if (noMoreArguments(args, index) || argumentStartsWithDashes(args, index)) {
    throw IllegalArgumentException("The argument following '--output' must be a valid directory name")
  }

  val file = File(args[index])
  if (file.exists() && !file.isDirectory) {
    throw IllegalArgumentException("The argument following '--output' ('${args[index]}') points to a file, not a directory")
  }

  return args[index]
}

private fun tryParsingEmailSubject(args: List<String>, index: Int): Pair<String, Int> {
  if (noMoreArguments(args, index) || argumentStartsWithDashes(args, index)) {
    throw IllegalArgumentException("The argument following '--email-subject' must be a valid String")
  }
  if (!args[index].startsWith("\"")) {
    return Pair(args[index], 1)
  } else {
    for (i in index until args.size) {
      if (args[i].endsWith("\"")) {
        return Pair(args.subList(index, i + 1).joinToString(" ").removeSurrounding("\""), i - index + 1)
      }
    }
  }
  throw IllegalArgumentException("The argument following '--email-subject' must be a valid String, enclosed \\\"like this\\\"")
}

fun tryParsingHouseholdNumbers(args: List<String>, index: Int): List<Int> {
  if (noMoreArguments(args, index) || argumentStartsWithDashes(args, index)) {
    throw IllegalArgumentException("The argument following '--household-numbers' must be a valid list of numbers")
  }

  val householdNumbers = args[index].replace(" ", "").split(",")
  try {
    return householdNumbers.filter{ it != ""}.map { Integer.parseInt(it) }
  } catch (e: Exception) {
    throw IllegalArgumentException("The argument following '--household-numbers' ('${args[index]}') must be a list of numbers")
  }
}

private fun argumentStartsWithDashes(args: List<String>, index: Int) = args[index].startsWith("--")

private fun noMoreArguments(args: List<String>, index: Int) = index >= args.size


private fun printHelpMessage() {
  log.info("\n== shipmember 1.1.0 ==\n" +
      "by Johan Sjöblom\n\n" +

      "A program for sending information to members of groups, associations etc. The program will read a csv " +
      "file with membership information, parse it and create personal messages for every recipient in the file.\n\n" +

      "Usage:\n" +
      "java -jar shipmember-1.1.0.jar <arguments>\n\n" +

      "Valid arguments:\n" +
      "'--input <inputfile>' Mandatory argument. Specifies the csv file with members to read from.\n" +
      "'--output <outputfolder>' Mandatory argument. Specifies where to save PDF files.\n" +
      "'--email-subject <string>' Optional argument. The subject of emails sent. Enclose multiple words \\\"Like this\\\".\n" +
      "'--household-numbers <list>' Optional argument. Only consider the given list of household numbers (integers). " +
      "Other arguments (such as '--only-non-payers') will apply in addition and may narrow the members down further.\n" +
      "'--only-non-payers' Optional argument. Makes the program only consider the members who have not paid. " +
      "Default (if this argument is not given) is that everyone is included.\n" +
      "'--parse-all' Optional argument. Every household is parsed. Unless narrowed down by other arguments, this is the default.\n" +
      "'--parse-those-with-emails' Optional argument. Only the households who have an email are parsed.\n" +
      "'--parse-those-without-emails' Optional argument. Only the households who do not have an email are parsed.\n" +
      "'--output-pdf-and-send-email' Optional argument. Will create a PDF for every household and send emails to every " +
      "household that has an email address. Unless overridden by other arguments, this is the default.\n" +
      "'--output-pdf-only' Optional argument. Will create a PDF for every household, but send no emails.\n" +
      "'--only-send-email-where-possible' Optional argument. If a household has an email address, it will be sent an email; " +
      "if not, a PDF is created instead.\n")
}
