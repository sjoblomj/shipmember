package org.sjoblomj.shipmember.outputters

import mu.KotlinLogging
import org.sjoblomj.shipmember.ShipMemberApplication
import org.sjoblomj.shipmember.dtos.Household
import org.springframework.core.io.FileSystemResource
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.Duration
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

private val log = KotlinLogging.logger {}

const val personalInfoFile = "personalinfo.tex"
private const val latexTemplate = "invite.tex"
private const val latexTemplateClassPathName = "templates/$latexTemplate"

var timeout = 30L
const val latexCompilationServerPort = 58404
const val baseUrl = "http://localhost:$latexCompilationServerPort"
const val url = "/compile"
private val webclient = WebClient.create(baseUrl)

fun createLatexFile(household: Household, outputDirectory: String) {
  val file = createNewFile(outputDirectory)

  val firstNames = household.getAllFirstNames().joinToString(", ")
  val surnames = household.getAllSurnames().joinToString(", ")
  write(file, "hasSeveralMembers", household.hasSeveralMembers().toString())
  write(file, "hasPaid", household.hasPaid().toString())
  write(file, "firstNames", firstNames)
  write(file, "surnames", surnames)
  write(file, "type", household.getType())
  write(file, "street", household.getStreet())
  write(file, "address", household.getAddress())
  write(file, "telephone", household.getFirstTelephone())
  write(file, "mobile", household.getFirstMobile())
  write(file, "email", household.getFirstEmail().replace("_", "\\_"))

  val fileName = if (household.hasSeveralMembers()) "$surnames ($firstNames)" else "$firstNames $surnames"

  log.debug { "Creating Latex file for $fileName" }
  createPdf(fileName, outputDirectory)
}

private fun createNewFile(outputDirectory: String): File {
  val file = File("$outputDirectory/$personalInfoFile")
  if (file.exists()) {
    file.writeText("")
  }
  return file
}

private fun write(file: File, key: String, value: String) {
  file.appendText("\\newcommand{\\member$key}{$value}\n")
}

private fun createPdf(fileName: String, outputDirectory: String) {
  copyLatexTemplateFromClasspathToFile(outputDirectory)
  val zipFile = createZipFileOfIndata(outputDirectory)

  val returnedBytes = sendIndataToPdfCompilationServer(zipFile)
  writePdfToDisk(fileName, outputDirectory, returnedBytes)
}

private fun writePdfToDisk(fileName: String, outputDirectory: String, returnedBytes: ByteArray) {
  val pdfName = sanitizeOutputFilename(fileName)
  val pdfFile = File("$outputDirectory/$pdfName.pdf")
  pdfFile.writeBytes(returnedBytes)

  if (!pdfFile.exists()) {
    log.error("Failed to create PDF for '$fileName'")
  }
}

private fun sendIndataToPdfCompilationServer(zipFile: File): ByteArray {
  val builder = MultipartBodyBuilder()
  builder.part("data", FileSystemResource(zipFile))
  builder.part("mainFile", "invite")

  return webclient.post()
          .uri(url)
          .body(BodyInserters.fromMultipartData(builder.build()))
          .retrieve()
          .onStatus(
                  { httpStatus -> httpStatus.is4xxClientError || httpStatus.is5xxServerError },
                  { response -> response.bodyToMono(String::class.java).map {
                    Exception("Got ${response.statusCode()} from Latex Compilation Server - response body: '$it'")
                  } } )
          .bodyToMono(ByteArray::class.java)
          .timeout(Duration.ofSeconds(timeout))
          .block()!!
}

fun createZipFileOfIndata(directory: String): File {
  val inputDirectory = File(directory)
  val outputZipFile = File.createTempFile("out", ".zip").also { it.deleteOnExit() }

  ZipOutputStream(BufferedOutputStream(FileOutputStream(outputZipFile))).use { zos ->
    inputDirectory.walkTopDown().forEach { file ->
      if (file.isFile && file.path.endsWith(".tex")) {
        val zipFileName = file.absolutePath.removePrefix(inputDirectory.absolutePath).removePrefix("/")
        val entry = ZipEntry(zipFileName)
        zos.putNextEntry(entry)
        if (file.isFile) {
          file.inputStream().copyTo(zos)
        }
      }
    }
  }
  return outputZipFile
}

private fun sanitizeOutputFilename(fileName: String) = fileName
        .replace("[^ (),0-9a-zA-ZéèåäöøüÉÈÅÄÖØÜ\\s]".toRegex(), "")
        .replace(" +".toRegex(), "_")


private fun copyLatexTemplateFromClasspathToFile(outputDirectory: String) {
  val classpathResource = ShipMemberApplication::class.java.classLoader.getResource(latexTemplateClassPathName)!!.readText()
  File("$outputDirectory/$latexTemplate").writeText(classpathResource)
}
