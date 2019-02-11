package org.sjoblomj.shipmember.outputters

import mu.KotlinLogging
import org.sjoblomj.shipmember.ShipMemberApplication
import org.sjoblomj.shipmember.dtos.Household
import java.io.File
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

const val personalInfoFile = "personalinfo.tex"
private const val latexTemplate = "invite.tex"
private const val latexTemplateClassPathName = "templates/$latexTemplate"

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
  createPdfAndCleanup(fileName, outputDirectory)
}

private fun createNewFile(outputDirectory: String): File {
  val file = File("$outputDirectory/$personalInfoFile")
  if (file.exists()) {
    file.writeText("")
  }
  return file
}

private fun write(file : File, key : String, value : String) {
  file.appendText("\\newcommand{\\member$key}{$value}\n")
}

private fun createPdfAndCleanup(fileName: String, outputDirectory: String) {
  val pdfName = sanitizeOutputFilename(fileName)
  createPdf(outputDirectory, pdfName)
  deleteLogAndAuxiliariesIfSuccess(outputDirectory, pdfName)

  if (!File("$outputDirectory/$pdfName.pdf").exists()) {
    log.error("Failed to create PDF for '$fileName'")
  }
}

private fun sanitizeOutputFilename(fileName: String): String {
  return fileName
      .replace("[^ (),0-9a-zA-ZéèåäöøüÉÈÅÄÖØÜ\\s]".toRegex(), "")
      .replace(" +".toRegex(), "_")
}

private fun createPdf(outputDirectory: String, pdfName: String) {
  try {
    copyLatexTemplateFromClasspathToFile(outputDirectory)

    val p = Runtime.getRuntime().exec("xelatex -output-directory=$outputDirectory -jobname=$pdfName $latexTemplate")
    p.waitFor(10, TimeUnit.SECONDS)

  } catch (e: Exception) {
    log.error("Something went wrong when trying to process '$pdfName'")
    e.printStackTrace()
  }
}

private fun copyLatexTemplateFromClasspathToFile(outputDirectory: String) {
  val classpathResource = ShipMemberApplication::class.java.classLoader.getResource(latexTemplateClassPathName).readText()
  File("$outputDirectory/$latexTemplate").writeText(classpathResource)
}

private fun deleteLogAndAuxiliariesIfSuccess(outputDirectory: String, baseFileName: String) {
  val baseName = "$outputDirectory/$baseFileName"
  if (File("$baseName.pdf").exists()) {
    File("$baseName.log").delete()
    File("$baseName.aux").delete()
    File("$outputDirectory/${personalInfoFile.replace("tex", "aux")}").delete()
  }
}
