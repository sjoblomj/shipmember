package org.sjoblomj.shipmember.parsers

import org.junit.Test
import org.sjoblomj.shipmember.dtos.MEMBERTYPES
import org.sjoblomj.shipmember.dtos.OUTPUTTYPES
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ArgumentParserTests {

  private val memberFile = "src/test/resources/members.csv"

  @Test fun `Empty arguments throws Exception`() {
    assertFailsWith(IllegalArgumentException::class) {
      parseArgs(emptyList())
    }
  }

  @Test fun `Input without file throws Exception`() {
    assertFailsWith(IllegalArgumentException::class) {
      parseArgs(listOf("--input"))
    }
  }

  @Test fun `Input with illegal file name throws Exception`() {
    assertFailsWith(IllegalArgumentException::class) {
      parseArgs(listOf("--input", "--illegalFileName.csv"))
    }
  }

  @Test fun `Input with non existing file throws Exception`() {
    assertFailsWith(IllegalArgumentException::class) {
      parseArgs(listOf("--input", "nonExistingFile.csv"))
    }
  }

  @Test fun `Output without file throws Exception`() {
    assertFailsWith(IllegalArgumentException::class) {
      parseArgs(listOf("--output"))
    }
  }

  @Test fun `Output with illegal file name throws Exception`() {
    assertFailsWith(IllegalArgumentException::class) {
      parseArgs(listOf("--output", "--illegalFileName.csv"))
    }
  }

  @Test fun `Output with existing file throws Exception`() {
    assertFailsWith(IllegalArgumentException::class) {
      parseArgs(listOf("--output", "src/test/resources/members.csv"))
    }
  }

  @Test fun `Household numbers without arguments`() {
    assertFailsWith(IllegalArgumentException::class) {
      parseArgs(listOf("--household-numbers"))
    }
  }

  @Test fun `Household numbers with non-numbers mixed in`() {
    assertFailsWith(IllegalArgumentException::class) {
      parseArgs(listOf("--household-numbers", "1,43,71,apa,38"))
    }
  }

  @Test fun `Household numbers with dashes`() {
    assertFailsWith(IllegalArgumentException::class) {
      parseArgs(listOf("--household-numbers", "--1"))
    }
  }

  @Test fun `Email subject single word`() {
    val args = parseArgs(listOf("--input", memberFile, "--output", "nonExistingDir", "--email-subject", "Apa"))

    assertEquals(memberFile, args.inputFile)
    assertEquals("nonExistingDir", args.outputDirectory)
    assertEquals("Apa", args.emailSubject)
  }

  @Test fun `Email subject single word with more arguments after`() {
    val args = parseArgs(listOf("--input", memberFile, "--output", "nonExistingDir", "--email-subject", "Apa", "--output-pdf-only"))

    assertEquals(memberFile, args.inputFile)
    assertEquals("nonExistingDir", args.outputDirectory)
    assertEquals("Apa", args.emailSubject)
    assertEquals(OUTPUTTYPES.PDF_ONLY, args.outputType)
  }

  @Test fun `Email subject single word with quotes with more arguments after`() {
    val args = parseArgs(listOf("--input", memberFile, "--output", "nonExistingDir", "--email-subject", "\"Apa\"", "--output-pdf-only"))

    assertEquals(memberFile, args.inputFile)
    assertEquals("nonExistingDir", args.outputDirectory)
    assertEquals("Apa", args.emailSubject)
    assertEquals(OUTPUTTYPES.PDF_ONLY, args.outputType)
  }

  @Test fun `Email subject multiple words`() {
    val args = parseArgs(listOf("--input", memberFile, "--output", "nonExistingDir", "--email-subject", "\"Apa", "Bepa", "Cepa\""))

    assertEquals(memberFile, args.inputFile)
    assertEquals("nonExistingDir", args.outputDirectory)
    assertEquals("Apa Bepa Cepa", args.emailSubject)
  }

  @Test fun `Email subject multiple words with more arguments after`() {
    val args = parseArgs(listOf("--input", memberFile, "--output", "nonExistingDir", "--email-subject", "\"Apa", "Bepa", "Cepa\"", "--output-pdf-only"))

    assertEquals(memberFile, args.inputFile)
    assertEquals("nonExistingDir", args.outputDirectory)
    assertEquals("Apa Bepa Cepa", args.emailSubject)
    assertEquals(OUTPUTTYPES.PDF_ONLY, args.outputType)
  }

  @Test fun `Email subject with space after quote`() {
    assertFailsWith(IllegalArgumentException::class) {
      parseArgs(listOf("--input", memberFile, "--output", "nonExistingDir", "--email-subject", "\"", "Apa", "Bepa", "Cepa\"", "--output-pdf-only"))
    }
  }

  @Test fun `Email subject without subject`() {
    assertFailsWith(IllegalArgumentException::class) {
      parseArgs(listOf("--input", memberFile, "--output", "nonExistingDir", "--email-subject"))
    }
  }

  @Test fun `Email subject multiple words without ending quote`() {
    assertFailsWith(IllegalArgumentException::class) {
      parseArgs(listOf("--input", memberFile, "--output", "nonExistingDir", "--email-subject", "\"Apa", "Bepa", "Cepa"))
    }
  }

  @Test fun `Input and output and nonExistingDir flag`() {
    val args = parseArgs(listOf("--input", memberFile, "--output", "nonExistingDir"))

    assertEquals(memberFile, args.inputFile)
    assertEquals("nonExistingDir", args.outputDirectory)
    assertFalse(args.onlyNonPayers)
  }

  @Test fun `Only non payers`() {
    val args = parseArgs(listOf("--only-non-payers", "--input", memberFile, "--output", "nonExistingDir"))

    assertTrue(args.onlyNonPayers)
  }

  @Test fun `Parse all`() {
    val args = parseArgs(listOf("--parse-all", "--input", memberFile, "--output", "nonExistingDir"))

    assertEquals(MEMBERTYPES.ALL, args.wantedMembers)
  }

  @Test fun `Parse those with email`() {
    val args = parseArgs(listOf("--parse-those-with-emails", "--input", memberFile, "--output", "nonExistingDir"))

    assertEquals(MEMBERTYPES.WITH_EMAILS, args.wantedMembers)
  }

  @Test fun `Parse those without email`() {
    val args = parseArgs(listOf("--parse-those-without-emails", "--input", memberFile, "--output", "nonExistingDir"))

    assertEquals(MEMBERTYPES.WITHOUT_EMAILS, args.wantedMembers)
  }

  @Test fun `Output Pdf and send email`() {
    val args = parseArgs(listOf("--output-pdf-and-send-email", "--input", memberFile, "--output", "nonExistingDir"))

    assertEquals(OUTPUTTYPES.PDF_AND_EMAIL, args.outputType)
  }

  @Test fun `Prefer email over Pdf`() {
    val args = parseArgs(listOf("--only-send-email-where-possible", "--input", memberFile, "--output", "nonExistingDir"))

    assertEquals(OUTPUTTYPES.EMAIL_OVER_PDF, args.outputType)
  }

  @Test fun `Output Pdf only`() {
    val args = parseArgs(listOf("--input", memberFile, "--output", "nonExistingDir", "--output-pdf-only"))

    assertEquals(OUTPUTTYPES.PDF_ONLY, args.outputType)
  }

  @Test fun `Household numbers with comma instead of number`() {
    val args = parseArgs(listOf("--input", memberFile, "--output", "nonExistingDir", "--household-numbers", ","))

    assertEquals(emptyList(), args.householdNumbers)
  }

  @Test fun `Household numbers with one number`() {
    val args = parseArgs(listOf("--input", memberFile, "--output", "nonExistingDir", "--household-numbers", "71"))

    assertEquals(listOf(71), args.householdNumbers)
  }

  @Test fun `Household numbers with one number ending in comma`() {
    val args = parseArgs(listOf("--input", memberFile, "--output", "nonExistingDir", "--household-numbers", "71,"))

    assertEquals(listOf(71), args.householdNumbers)
  }

  @Test fun `Household numbers`() {
    val args = parseArgs(listOf("--input", memberFile, "--output", "nonExistingDir", "--household-numbers", "1,2,71,38"))

    assertEquals(listOf(1, 2, 71, 38), args.householdNumbers)
  }

  @Test fun `Household numbers with argument after`() {
    val args = parseArgs(listOf("--input", memberFile, "--output", "nonExistingDir", "--household-numbers", "1,2,71,38", "--output-pdf-only"))

    assertEquals(listOf(1, 2, 71, 38), args.householdNumbers)
    assertEquals(OUTPUTTYPES.PDF_ONLY, args.outputType)
  }

  @Test fun `UnknownArgument throws Exception`() {
    assertFailsWith(IllegalArgumentException::class) {
      parseArgs(listOf("--unknownArgument"))
    }
  }
}
