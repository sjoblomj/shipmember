package org.sjoblomj.shipmember.parsers

import mu.KotlinLogging
import org.sjoblomj.shipmember.dtos.Household
import org.sjoblomj.shipmember.dtos.Member
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.IntStream
import java.util.stream.Stream
import kotlin.streams.toList

private val log = KotlinLogging.logger {}

private const val separator = "\t"
private const val defaultMemberType = "Enskild medlem"

private fun getType(type: String) : String? {
  return when (type.toLowerCase()) {
    "stu"      -> "Student"
    "ung"      -> "Ungdom"
    "hed"      -> "Hedersmedlem"
    "f", "fh"  -> "Familj"
    "jur.per." -> "Juridisk person"
    ""         -> defaultMemberType
    else       -> null
  }
}

fun parseFile(filename: String) : List<Household> {
  val households = Files.lines(Paths.get(filename)).use { stream -> processFile(stream) }
  log.info("There are ${households.size} households in $filename")
  return households
}

private fun processFile(lines: Stream<String>): List<Household> {
  val members = getMemberList(lines)
  return memberListToHouseholdList(members)
}

private fun getMemberList(lines: Stream<String>): List<Member> {
  return lines
      .filter { !it.isBlank() }
      .map { createMember(it) }
      .toList()
      .filterNotNull()
}

fun createMember(line: String) : Member? {
  try {
    val fields = line.split(separator).map { it.trim() }.map { it.replace("\\s+".toRegex(), " ") }

    val householdNo = Integer.parseInt(fields[0])
    val firstName   = fields[1]
    val surname     = fields[2]
    val type        = fields[3]
    val street      = fields[4]
    val address     = fields[5]
    val telephone   = fields[6]
    val mobile      = fields[7]
    val email       = fields[8].split(";")[0].trim()
    val paid        = fields[9]

    val hasPaid = if (paid.toLowerCase() == "true" || paid == "1") "true" else "false"
    val memberType = getMemberType(type, firstName, surname)

    return Member(householdNo, firstName, surname, memberType, street, address, telephone, mobile, email, hasPaid)

  } catch (e : Exception) {

    log.warn("Will ignore the following line since it could not be parsed: '$line'")
    return null
  }
}

private fun getMemberType(typeAbbreviation: String, firstName: String, surname: String): String {
  val type = getType(typeAbbreviation)
  if (type == null) {
    log.warn("Warning! Could not understand member type '$typeAbbreviation' for member $firstName $surname -- Will treat as '$defaultMemberType'\n")
  }
  return type ?: defaultMemberType
}

private fun memberListToHouseholdList(members: List<Member>): List<Household> {

  return IntStream.range(0, getMaxHouseholdNumber(members) + 1)
      .mapToObj { householdNumber -> members.filter { it.householdNo == householdNumber } }
      .filter { listOfHouseholdMembers -> listOfHouseholdMembers.isNotEmpty() }
      .map { listOfHouseholdMembers -> Household(listOfHouseholdMembers) }
      .toList()
}

private fun getMaxHouseholdNumber(members: List<Member>): Int {
  return members.stream()
      .mapToInt { it.householdNo }
      .max()
      .orElse(1024)
}
