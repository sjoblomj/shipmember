package org.sjoblomj.shipmember.dtos

import javax.mail.internet.InternetAddress
import kotlin.reflect.KProperty1

class Household(val members: List<Member>) {

  init {
    if (members.isEmpty()) {
      throw IllegalArgumentException("Encountered a household with no members!")
    }
    val valuesThatMustNotDifferWithinHousehold = listOf(
        Pair(Member::householdNo, "householdNo"),
        Pair(Member::type, "membership type"),
        Pair(Member::hasPaid, "paid")
    )
    assertExactlyOneValue(valuesThatMustNotDifferWithinHousehold)

    val streetAddress = listOf(
        Pair(Member::street, "street"),
        Pair(Member::address, "address")
    )
    assertAtMostOneValue(streetAddress)

    val valuesThatMustNotBeBlank = listOf(
        Pair(Member::firstName, "first name"),
        Pair(Member::surname, "surname"),
        Pair(Member::type, "membership type")
    )
    assertValuesNotBlank(valuesThatMustNotBeBlank)

    if (hasSeveralMembers() && members[0].type != "Familj") {
      throw IllegalArgumentException("The household with number ${members[0].householdNo} are not all of membership type 'Familj'")
    }
    assertEmailIsValid()
  }

  private fun assertEmailIsValid() {
    val email = getFirstEmail()
    if (email != "") {
      try {
        InternetAddress(email).validate()
        if (!email.contains(".")) {
          throw IllegalArgumentException("Email must contain '.'")
        }
      } catch (e: Exception) {
        throw IllegalArgumentException("Email '$email' did not validate")
      }
    }
  }

  private fun assertExactlyOneValue(list: List<Pair<KProperty1<Member, Any>, String>>) {
    for (pair in list) {
      val property = pair.first
      val msg = pair.second

      val numberOfNonBlankValuesPresent = findNumberOfNonBlankValuesPresent(property)
      if (numberOfNonBlankValuesPresent == 0) {
        throw IllegalArgumentException("The household with number ${members[0].householdNo} has no $msg values")
      } else if (numberOfNonBlankValuesPresent > 1) {
        throw IllegalArgumentException("The household with number ${members[0].householdNo} has different $msg values")
      }
    }
  }

  private fun assertAtMostOneValue(list: List<Pair<KProperty1<Member, Any>, String>>) {
    for (pair in list) {
      val property = pair.first
      val msg = pair.second

      val numberOfNonBlankValuesPresent = findNumberOfNonBlankValuesPresent(property)
      if (numberOfNonBlankValuesPresent > 1) {
        throw IllegalArgumentException("The household with number ${members[0].householdNo} has different $msg values")
      }
    }
  }

  private fun assertValuesNotBlank(list: List<Pair<KProperty1<Member, String>, String>>) {
    for (pair in list) {
      val property = pair.first
      val msg = pair.second

      if (findNumberOfBlankValuesPresent(property) != 0) {
        throw IllegalArgumentException("The household with number ${members[0].householdNo} has blank values for property '$msg'")
      }
    }
  }

  fun hasSeveralMembers(): Boolean {
    return members.size >= 2
  }

  fun hasPaid(): Boolean {
    return members.map { it.hasPaid() }.first()
  }

  fun getHouseholdNumber(): Int {
    return members.map { it.householdNo }.first()
  }

  fun getType(): String {
    return getFirstProperty(Member::type)
  }

  fun getStreet(): String {
    return getFirstProperty(Member::street)
  }

  fun getAddress(): String {
    return getFirstProperty(Member::address)
  }

  fun getFirstEmail(): String {
    return getFirstProperty(Member::email)
  }

  fun getFirstMobile(): String {
    return getFirstProperty(Member::mobile)
  }

  fun getFirstTelephone(): String {
    return getFirstProperty(Member::telephone)
  }

  private fun getFirstProperty(property: KProperty1<Member, String>): String {
    return members
        .map { property.get(it) }
        .firstOrNull { it != "" }
        ?: ""
  }

  fun getAllFirstNames(): List<String> {
    return members.map { it.firstName }
  }

  fun getAllSurnames(): List<String> {
    return members
        .flatMap { it.surname.split(" ") }
        .distinct()
  }

  private fun findNumberOfNonBlankValuesPresent(property: KProperty1<Member, Any>): Int {
    return members
        .map { property.get(it) }
        .filter { it != "" }
        .distinct()
        .count()
  }

  private fun findNumberOfBlankValuesPresent(property: KProperty1<Member, String>): Int {
    return members
        .map { property.get(it) }
        .filter { it == "" }
        .count()
  }

  override fun toString(): String {
    return "${members.size}: " + members.joinToString { "${it.firstName} ${it.surname}" }
  }
}
