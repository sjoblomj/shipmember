package org.sjoblomj.shipmember

import com.github.sleroy.fakesmtp.core.ServerConfiguration
import com.github.sleroy.junit.mail.server.test.FakeSmtpRule
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.sjoblomj.shipmember.dtos.Arguments
import org.sjoblomj.shipmember.dtos.EmailSettings
import org.sjoblomj.shipmember.dtos.MEMBERTYPES
import org.sjoblomj.shipmember.dtos.OUTPUTTYPES
import org.sjoblomj.shipmember.outputters.latexCompilationServerPort
import org.sjoblomj.shipmember.outputters.url
import org.springframework.http.MediaType
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MainServiceTests {

  private val wiremockServer = WireMockServer(latexCompilationServerPort)
  private val smtpPort = 2525

  private val inputFile = "src/test/resources/members.csv"
  private val outputDirectory = "target/testfiles/"

  @get:Rule
  val smtpServer = FakeSmtpRule(ServerConfiguration.create()
      .port(smtpPort)
      .relayDomains("apabepa.com")
      .charset("UTF-8"))

  private val emailSettings = EmailSettings(false, "localhost", smtpPort, "apa", "bepa", "subject")

  @Before fun setup() {
    wiremockServer.start()
    mockServer()
    File(outputDirectory).mkdir()

    assertTrue(smtpServer.isRunning)
  }

  @After fun teardown() {
    File(outputDirectory).deleteRecursively()
    wiremockServer.stop()
  }

  @Test fun `Only non payers - All member types - Email over PDF`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", true, MEMBERTYPES.ALL, OUTPUTTYPES.EMAIL_OVER_PDF)

    notifyMembers(emailSettings, arguments)

    assertEquals(listOf("sa@apabepa.com"), smtpServer.mailBox().map { it.to })
    assertOutputDirectoryContains(listOf("SteinBjarne_Østgran.pdf"))
  }

  @Test fun `Only non payers - All member types - PDF only`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", true, MEMBERTYPES.ALL, OUTPUTTYPES.PDF_ONLY)

    notifyMembers(emailSettings, arguments)

    assertTrue(smtpServer.mailBox().isEmpty())
    assertOutputDirectoryContains(listOf("Sophia_Andrésen.pdf", "SteinBjarne_Østgran.pdf"))
  }

  @Test fun `Only non payers - All member types - PDF and email`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", true, MEMBERTYPES.ALL, OUTPUTTYPES.PDF_AND_EMAIL)

    notifyMembers(emailSettings, arguments)

    assertEquals(listOf("sa@apabepa.com"), smtpServer.mailBox().map { it.to })
    assertOutputDirectoryContains(listOf("Sophia_Andrésen.pdf", "SteinBjarne_Østgran.pdf"))
  }

  @Test fun `Only non payers - All member types - PDF and email - Only certain household numbers`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", true, MEMBERTYPES.ALL, OUTPUTTYPES.PDF_AND_EMAIL, listOf(6, 7))

    notifyMembers(emailSettings, arguments)

    assertTrue(smtpServer.mailBox().isEmpty())
    assertOutputDirectoryContains(listOf("SteinBjarne_Østgran.pdf"))
  }


  @Test fun `Only non payers - Members without email - Email over PDF`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", true, MEMBERTYPES.WITHOUT_EMAILS, OUTPUTTYPES.EMAIL_OVER_PDF)

    notifyMembers(emailSettings, arguments)

    assertTrue(smtpServer.mailBox().isEmpty())
    assertOutputDirectoryContains(listOf("SteinBjarne_Østgran.pdf"))
  }

  @Test fun `Only non payers - Members without email - PDF only`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", true, MEMBERTYPES.WITHOUT_EMAILS, OUTPUTTYPES.PDF_ONLY)

    notifyMembers(emailSettings, arguments)

    assertTrue(smtpServer.mailBox().isEmpty())
    assertOutputDirectoryContains(listOf("SteinBjarne_Østgran.pdf"))
  }

  @Test fun `Only non payers - Members without email - PDF and email`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", true, MEMBERTYPES.WITHOUT_EMAILS, OUTPUTTYPES.PDF_AND_EMAIL)

    notifyMembers(emailSettings, arguments)

    assertTrue(smtpServer.mailBox().isEmpty())
    assertOutputDirectoryContains(listOf("SteinBjarne_Østgran.pdf"))
  }

  @Test fun `Only non payers - Members without email - PDF and email - Only certain household numbers`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", true, MEMBERTYPES.WITHOUT_EMAILS, OUTPUTTYPES.PDF_AND_EMAIL, listOf(7, 9))

    notifyMembers(emailSettings, arguments)

    assertTrue(smtpServer.mailBox().isEmpty())
    assertOutputDirectoryContains(listOf("SteinBjarne_Østgran.pdf"))
  }


  @Test fun `Only non payers - Members with email - Email over PDF`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", true, MEMBERTYPES.WITH_EMAILS, OUTPUTTYPES.EMAIL_OVER_PDF)

    notifyMembers(emailSettings, arguments)

    assertEquals(listOf("sa@apabepa.com"), smtpServer.mailBox().map { it.to })
    assertOutputDirectoryContains(emptyList())
  }

  @Test fun `Only non payers - Members with email - PDF only`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", true, MEMBERTYPES.WITH_EMAILS, OUTPUTTYPES.PDF_ONLY)

    notifyMembers(emailSettings, arguments)

    assertTrue(smtpServer.mailBox().isEmpty())
    assertOutputDirectoryContains(listOf("Sophia_Andrésen.pdf"))
  }

  @Test fun `Only non payers - Members with email - PDF and email`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", true, MEMBERTYPES.WITH_EMAILS, OUTPUTTYPES.PDF_AND_EMAIL)

    notifyMembers(emailSettings, arguments)

    assertEquals(listOf("sa@apabepa.com"), smtpServer.mailBox().map { it.to })
    assertOutputDirectoryContains(listOf("Sophia_Andrésen.pdf"))
  }

  @Test fun `Only non payers - Members with email - PDF and email - Only certain household numbers`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", true, MEMBERTYPES.WITH_EMAILS, OUTPUTTYPES.PDF_AND_EMAIL, listOf(6, 7))

    notifyMembers(emailSettings, arguments)

    assertTrue(smtpServer.mailBox().isEmpty())
    assertOutputDirectoryContains(emptyList())
  }


  ////


  @Test fun `Not only non payers - All member types - Email over PDF`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", false, MEMBERTYPES.ALL, OUTPUTTYPES.EMAIL_OVER_PDF)

    notifyMembers(emailSettings, arguments)

    assertEquals(listOf("maal@apabepa.com", "stan@apabepa.com", "sa@apabepa.com", "gm@apabepa.com"), smtpServer.mailBox().map { it.to })
    assertOutputDirectoryContains(listOf("SteinBjarne_Østgran.pdf", "GubbGunnar_Gammelgubbe.pdf"))
  }

  @Test fun `Not only non payers - All member types - PDF only`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", false, MEMBERTYPES.ALL, OUTPUTTYPES.PDF_ONLY)

    notifyMembers(emailSettings, arguments)

    assertTrue(smtpServer.mailBox().isEmpty())
    assertOutputDirectoryContains(listOf("Sophia_Andrésen.pdf", "SteinBjarne_Østgran.pdf", "GubbGunnar_Gammelgubbe.pdf",
        "Algren,_Stengren_(Petter,_Malin,_Staffan,_Nils,_Lena).pdf", "Stina_Andropovich.pdf", "Greta_Malmhalm.pdf"))
  }

  @Test fun `Not only non payers - All member types - PDF and email`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", false, MEMBERTYPES.ALL, OUTPUTTYPES.PDF_AND_EMAIL)

    notifyMembers(emailSettings, arguments)

    assertEquals(listOf("maal@apabepa.com", "stan@apabepa.com", "sa@apabepa.com", "gm@apabepa.com"), smtpServer.mailBox().map { it.to })
    assertOutputDirectoryContains(listOf("Sophia_Andrésen.pdf", "SteinBjarne_Østgran.pdf", "GubbGunnar_Gammelgubbe.pdf",
        "Algren,_Stengren_(Petter,_Malin,_Staffan,_Nils,_Lena).pdf", "Stina_Andropovich.pdf", "Greta_Malmhalm.pdf"))
  }

  @Test fun `Not only non payers - All member types - PDF and email - Only certain household numbers`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", false, MEMBERTYPES.ALL, OUTPUTTYPES.PDF_AND_EMAIL, listOf(1, 7, 9))

    notifyMembers(emailSettings, arguments)

    assertEquals(listOf("maal@apabepa.com"), smtpServer.mailBox().map { it.to })
    assertOutputDirectoryContains(listOf("SteinBjarne_Østgran.pdf", "GubbGunnar_Gammelgubbe.pdf",
        "Algren,_Stengren_(Petter,_Malin,_Staffan,_Nils,_Lena).pdf"))
  }


  @Test fun `Not only non payers - Members without email - Email over PDF`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", false, MEMBERTYPES.WITHOUT_EMAILS, OUTPUTTYPES.EMAIL_OVER_PDF)

    notifyMembers(emailSettings, arguments)

    assertTrue(smtpServer.mailBox().isEmpty())
    assertOutputDirectoryContains(listOf("SteinBjarne_Østgran.pdf", "GubbGunnar_Gammelgubbe.pdf"))
  }

  @Test fun `Not only non payers - Members without email - PDF only`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", false, MEMBERTYPES.WITHOUT_EMAILS, OUTPUTTYPES.PDF_ONLY)

    notifyMembers(emailSettings, arguments)

    assertTrue(smtpServer.mailBox().isEmpty())
    assertOutputDirectoryContains(listOf("SteinBjarne_Østgran.pdf", "GubbGunnar_Gammelgubbe.pdf"))
  }

  @Test fun `Not only non payers - Members without email - PDF and email`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", false, MEMBERTYPES.WITHOUT_EMAILS, OUTPUTTYPES.PDF_AND_EMAIL)

    notifyMembers(emailSettings, arguments)

    assertTrue(smtpServer.mailBox().isEmpty())
    assertOutputDirectoryContains(listOf("SteinBjarne_Østgran.pdf", "GubbGunnar_Gammelgubbe.pdf"))
  }

  @Test fun `Not only non payers - Members without email - PDF and email - Only certain household numbers`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", false, MEMBERTYPES.WITHOUT_EMAILS, OUTPUTTYPES.PDF_AND_EMAIL, listOf(9))

    notifyMembers(emailSettings, arguments)

    assertTrue(smtpServer.mailBox().isEmpty())
    assertOutputDirectoryContains(listOf("GubbGunnar_Gammelgubbe.pdf"))
  }


  @Test fun `Not only non payers - Members with email - Email over PDF`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", false, MEMBERTYPES.WITH_EMAILS, OUTPUTTYPES.EMAIL_OVER_PDF)

    notifyMembers(emailSettings, arguments)

    assertEquals(listOf("maal@apabepa.com", "stan@apabepa.com", "sa@apabepa.com", "gm@apabepa.com"), smtpServer.mailBox().map { it.to })
    assertOutputDirectoryContains(emptyList())
  }

  @Test fun `Not only non payers - Members with email - PDF only`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", false, MEMBERTYPES.WITH_EMAILS, OUTPUTTYPES.PDF_ONLY)

    notifyMembers(emailSettings, arguments)

    assertTrue(smtpServer.mailBox().isEmpty())
    assertOutputDirectoryContains(listOf("Sophia_Andrésen.pdf", "Algren,_Stengren_(Petter,_Malin,_Staffan,_Nils,_Lena).pdf", "Stina_Andropovich.pdf", "Greta_Malmhalm.pdf"))
  }

  @Test fun `Not only non payers - Members with email - PDF and email`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", false, MEMBERTYPES.WITH_EMAILS, OUTPUTTYPES.PDF_AND_EMAIL)

    notifyMembers(emailSettings, arguments)

    assertEquals(listOf("maal@apabepa.com", "stan@apabepa.com", "sa@apabepa.com", "gm@apabepa.com"), smtpServer.mailBox().map { it.to })
    assertOutputDirectoryContains(listOf("Sophia_Andrésen.pdf", "Algren,_Stengren_(Petter,_Malin,_Staffan,_Nils,_Lena).pdf", "Stina_Andropovich.pdf", "Greta_Malmhalm.pdf"))
  }

  @Test fun `Not only non payers - Members with email - PDF and email - Only certain household numbers`() {
    assertTrue(smtpServer.mailBox().isEmpty())
    val arguments = Arguments(inputFile, outputDirectory, "subject", false, MEMBERTYPES.WITH_EMAILS, OUTPUTTYPES.PDF_AND_EMAIL, listOf(1, 8))

    notifyMembers(emailSettings, arguments)

    assertEquals(listOf("maal@apabepa.com", "sa@apabepa.com"), smtpServer.mailBox().map { it.to })
    assertOutputDirectoryContains(listOf("Sophia_Andrésen.pdf", "Algren,_Stengren_(Petter,_Malin,_Staffan,_Nils,_Lena).pdf"))
  }


  private fun assertOutputDirectoryContains(content: List<String>) {
    assertTrue(content.containsAll(getPdfsInOutputDirectory()))
  }

  private fun getPdfsInOutputDirectory() = File(outputDirectory).listFiles()
        ?.map { it.name }
        ?.filter { it.endsWith(".pdf") }
        ?: emptyList()


  private fun mockServer(mockedStatus: Int = 200, delay: Int = 0) {
    wiremockServer.stubFor(
            WireMock.post(WireMock.urlEqualTo(url))
                    .willReturn(WireMock.aResponse()
                            .withHeader("Content-Type", MediaType.APPLICATION_PDF.toString())
                            .withBody("mocked_response".toByteArray())
                            .withStatus(mockedStatus)
                            .withFixedDelay(delay)))
  }
}
