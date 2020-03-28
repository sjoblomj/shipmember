package org.sjoblomj.shipmember.outputters

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.sjoblomj.shipmember.dtos.Household
import org.sjoblomj.shipmember.dtos.Member
import java.io.File
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LatexOutputterTests {

  private val outputDirectory = "target/testfiles/"
  private val latexCommand = "\\newcommand{\\"

  private val member = Member(1, "Apa", "Bepa", "Enskild medlem", "Apabepastraße 71", "123 City", "71", "1234", "apabepacepa@mailinator.com", "0")

  private val household = Household(listOf(
      member.copy(firstName = "Apa", type = "Familj", street = "", address = "", telephone = "71", mobile = "", email = ""),
      member.copy(firstName = "Bepa", type = "Familj"),
      member.copy(firstName = "Cepa", surname = "Fepa", type = "Familj", mobile = "2345", email = ""),
      member.copy(firstName = "Depa Epa", surname = "Apa Bepa", type = "Familj", mobile = "3456", email = "")
  ))

  @Before fun setup() {
    File(outputDirectory).mkdir()
  }

  @After fun teardown() {
    File(outputDirectory).deleteRecursively()
  }

  @Test fun `Single member with all values`() {
    createLatexFile(Household(listOf(member)), outputDirectory)

    val content = readFileContent()
    assertTrue(content.contains("${latexCommand}memberfirstNames}{Apa}"))
    assertTrue(content.contains("${latexCommand}membersurnames}{Bepa}"))
    assertTrue(content.contains("${latexCommand}membertype}{Enskild medlem}"))
    assertTrue(content.contains("${latexCommand}memberstreet}{Apabepastraße 71}"))
    assertTrue(content.contains("${latexCommand}memberaddress}{123 City}"))
    assertTrue(content.contains("${latexCommand}membertelephone}{71}"))
    assertTrue(content.contains("${latexCommand}membermobile}{1234}"))
    assertTrue(content.contains("${latexCommand}memberemail}{apabepacepa@mailinator.com}"))
    assertTrue(content.contains("${latexCommand}memberhasPaid}{false}"))
    assertTrue(content.contains("${latexCommand}memberhasSeveralMembers}{false}"))

    assertTrue(File("$outputDirectory/Apa_Bepa.pdf").exists())
  }

  @Test fun `Single member with minimum amount of values`() {
    val minMember = member.copy(telephone = "", mobile = "", email = "", hasPaid = "true")
    createLatexFile(Household(listOf(minMember)), outputDirectory)

    val content = readFileContent()
    assertTrue(content.contains("${latexCommand}memberfirstNames}{Apa}"))
    assertTrue(content.contains("${latexCommand}membersurnames}{Bepa}"))
    assertTrue(content.contains("${latexCommand}membertype}{Enskild medlem}"))
    assertTrue(content.contains("${latexCommand}memberstreet}{Apabepastraße 71}"))
    assertTrue(content.contains("${latexCommand}memberaddress}{123 City}"))
    assertTrue(content.contains("${latexCommand}membertelephone}{}"))
    assertTrue(content.contains("${latexCommand}membermobile}{}"))
    assertTrue(content.contains("${latexCommand}memberemail}{}"))
    assertTrue(content.contains("${latexCommand}memberhasPaid}{true}"))
    assertTrue(content.contains("${latexCommand}memberhasSeveralMembers}{false}"))

    assertTrue(File("$outputDirectory/Apa_Bepa.pdf").exists())
  }

  @Test fun `Family with all values`() {
    createLatexFile(household, outputDirectory)

    val content = readFileContent()
    assertTrue(content.contains("${latexCommand}memberfirstNames}{Apa, Bepa, Cepa, Depa Epa}"))
    assertTrue(content.contains("${latexCommand}membersurnames}{Bepa, Fepa, Apa}"))
    assertTrue(content.contains("${latexCommand}membertype}{Familj}"))
    assertTrue(content.contains("${latexCommand}memberstreet}{Apabepastraße 71}"))
    assertTrue(content.contains("${latexCommand}memberaddress}{123 City}"))
    assertTrue(content.contains("${latexCommand}membertelephone}{71}"))
    assertTrue(content.contains("${latexCommand}membermobile}{1234}"))
    assertTrue(content.contains("${latexCommand}memberemail}{apabepacepa@mailinator.com}"))
    assertTrue(content.contains("${latexCommand}memberhasPaid}{false}"))
    assertTrue(content.contains("${latexCommand}memberhasSeveralMembers}{true}"))

    assertTrue(File("$outputDirectory/Bepa,_Fepa,_Apa_(Apa,_Bepa,_Cepa,_Depa_Epa).pdf").exists())
  }

  @Test fun `Family with minimum amount of values`() {
    val household = Household(listOf(
        member.copy(firstName = "Apa", type = "Familj", street = "", address = "", telephone = "", mobile = "", email = ""),
        member.copy(firstName = "Bepa", type = "Familj", telephone = "", mobile = "", email = ""),
        member.copy(firstName = "Cepa", surname = "Fepa", type = "Familj", telephone = "", mobile = "", email = ""),
        member.copy(firstName = "Depa Epa", surname = "Apa Bepa", type = "Familj", telephone = "", mobile = "", email = "")
    ))
    createLatexFile(household, outputDirectory)

    val content = readFileContent()
    assertTrue(content.contains("${latexCommand}memberfirstNames}{Apa, Bepa, Cepa, Depa Epa}"))
    assertTrue(content.contains("${latexCommand}membersurnames}{Bepa, Fepa, Apa}"))
    assertTrue(content.contains("${latexCommand}membertype}{Familj}"))
    assertTrue(content.contains("${latexCommand}memberstreet}{Apabepastraße 71}"))
    assertTrue(content.contains("${latexCommand}memberaddress}{123 City}"))
    assertTrue(content.contains("${latexCommand}membertelephone}{}"))
    assertTrue(content.contains("${latexCommand}membermobile}{}"))
    assertTrue(content.contains("${latexCommand}memberemail}{}"))
    assertTrue(content.contains("${latexCommand}memberhasPaid}{false}"))
    assertTrue(content.contains("${latexCommand}memberhasSeveralMembers}{true}"))

    assertTrue(File("$outputDirectory/Bepa,_Fepa,_Apa_(Apa,_Bepa,_Cepa,_Depa_Epa).pdf").exists())
  }

  @Test fun `Single member with weird name`() {
    createLatexFile(Household(listOf(member.copy(firstName = "Apa  / Bepa @ cepa | Östen", surname = "Ax:son Øysteinsson Ängå"))), outputDirectory)

    val content = readFileContent()
    assertTrue(content.contains("${latexCommand}memberfirstNames}{Apa  / Bepa @ cepa | Östen}"))
    assertTrue(content.contains("${latexCommand}membersurnames}{Ax:son, Øysteinsson, Ängå}"))

    assertTrue(File("$outputDirectory/Apa_Bepa_cepa_Östen_Axson,_Øysteinsson,_Ängå.pdf").exists())
  }

  @Test fun `Single member with underscore in email`() {
    createLatexFile(Household(listOf(member.copy(email = "Apa_Bepa@mailinator.com"))), outputDirectory)

    val content = readFileContent()
    assertTrue(content.contains("${latexCommand}memberfirstNames}{Apa}"))
    assertTrue(content.contains("${latexCommand}membersurnames}{Bepa}"))
    assertTrue(content.contains("${latexCommand}membertype}{Enskild medlem}"))
    assertTrue(content.contains("${latexCommand}memberstreet}{Apabepastraße 71}"))
    assertTrue(content.contains("${latexCommand}memberaddress}{123 City}"))
    assertTrue(content.contains("${latexCommand}membertelephone}{71}"))
    assertTrue(content.contains("${latexCommand}membermobile}{1234}"))
    assertTrue(content.contains("${latexCommand}memberemail}{Apa\\_Bepa@mailinator.com}"))
    assertTrue(content.contains("${latexCommand}memberhasPaid}{false}"))
    assertTrue(content.contains("${latexCommand}memberhasSeveralMembers}{false}"))

    assertTrue(File("$outputDirectory/Apa_Bepa.pdf").exists())
  }

  @Test fun `Log file and auxiliaries are deleted on success`() {
    createLatexFile(Household(listOf(member)), outputDirectory)

    assertTrue(File("$outputDirectory/Apa_Bepa.pdf").exists())
    assertFalse(File("$outputDirectory/Apa_Bepa.log").exists())
    assertFalse(File("$outputDirectory/Apa_Bepa.aux").exists())
  }

  @Test fun `Single member with illegal characters -- log file and auxiliaries are not deleted`() {
    createLatexFile(Household(listOf(member.copy(firstName = "Apa}"))), outputDirectory)

    assertFalse(File("$outputDirectory/Apa_Bepa.pdf").exists())
    assertTrue(File("$outputDirectory/Apa_Bepa.log").exists())
  }

  @Test fun `Personal Info File already exists -- is deleted`() {
    val fileContent = "The personal info file already exists and has some old content"
    val file = File("$outputDirectory/$personalInfoFile")
    file.writeText(fileContent)

    createLatexFile(household, outputDirectory)

    val content = readFileContent()
    assertFalse(content.contains(fileContent))
  }

  private fun readFileContent(): String {
    val file = File("$outputDirectory/$personalInfoFile")
    assertTrue(file.exists())
    return file.readText()
  }
}
