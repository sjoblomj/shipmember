package org.sjoblomj.shipmember.dtos

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HouseholdTests {

  private val member = Member(1, "Apa", "Bepa", "Familj", "Apabepastraße 71", "123 City", "", "1234", "cepa@bepa.apa", "0")

  private val household = Household(listOf(
      member.copy(firstName = "Apa", street = "", address = "", telephone = "", mobile = "", email = ""),
      member.copy(firstName = "Bepa"),
      member.copy(firstName = "Cepa", surname = "Fepa", mobile = "2345", email = ""),
      member.copy(firstName = "Depa Epa", surname = "Apa Bepa", mobile = "3456", email = "")
  ))

  @Test fun `Can create household of one member`() {
    Household(listOf(member.copy(type = "Hedersmedlem")))
  }

  @Test fun `Household with no members`() {
    assertFailsWith(IllegalArgumentException::class) {
      Household(emptyList())
    }
  }

  @Test fun `Different streets throws Exception`() {
    val street1 = "Some street 1"
    val street2 = "Different road 2"

    assertFailsWith(IllegalArgumentException::class) {
      Household(listOf(
          member.copy(firstName = "Apa", street = street1),
          member.copy(firstName = "Bepa", street = street2)
      ))
    }
  }

  @Test fun `Different cities throws Exception`() {
    val city1 = "431 Göteborg"
    val city2 = "0551 Oslo"

    assertFailsWith(IllegalArgumentException::class) {
      Household(listOf(
          member.copy(firstName = "Apa", address = city1),
          member.copy(firstName = "Bepa", address = city2)
      ))
    }
  }

  @Test fun `Different payments throws Exception`() {
    val hasPaid1 = "1"
    val hasPaid2 = "0"

    assertFailsWith(IllegalArgumentException::class) {
      Household(listOf(
          member.copy(firstName = "Apa", hasPaid = hasPaid1),
          member.copy(firstName = "Bepa", hasPaid = hasPaid2)
      ))
    }
  }

  @Test fun `Different membership types throws Exception`() {
    val type1 = "Familj"
    val type2 = "Hedersmedlem"

    assertFailsWith(IllegalArgumentException::class) {
      Household(listOf(
          member.copy(firstName = "Apa", type = type1),
          member.copy(firstName = "Bepa", type = type2)
      ))
    }
  }

  @Test fun `Different household numbers throws Exception`() {
    val householdNo1 = 1
    val householdNo2 = 2

    assertFailsWith(IllegalArgumentException::class) {
      Household(listOf(
          member.copy(firstName = "Apa", householdNo = householdNo1),
          member.copy(firstName = "Bepa", householdNo = householdNo2)
      ))
    }
  }


  @Test fun `If there are several household members, the membership type must be 'Familj'`() {
    val type = "Enskild medlem"

    assertFailsWith(IllegalArgumentException::class) {
      Household(listOf(
          member.copy(firstName = "Apa", type = type),
          member.copy(firstName = "Bepa", type = type)
      ))
    }
  }

  @Test fun `All first names must have values`() {
    val name1 = "Apa"
    val name2 = ""

    assertFailsWith(IllegalArgumentException::class) {
      Household(listOf(
          member.copy(firstName = name1),
          member.copy(firstName = name2)
      ))
    }
  }

  @Test fun `All surnames must have values`() {
    val name1 = "Cepa"
    val name2 = ""

    assertFailsWith(IllegalArgumentException::class) {
      Household(listOf(
          member.copy(firstName = "Apa", surname = name1),
          member.copy(firstName = "Bepa", surname = name2)
      ))
    }
  }

  @Test fun `All membership types must have values`() {
    val type1 = "Familj"
    val type2 = ""

    assertFailsWith(IllegalArgumentException::class) {
      Household(listOf(
          member.copy(firstName = "Apa", type = type1),
          member.copy(firstName = "Bepa", type = type2)
      ))
    }
  }

  @Test fun `Some streets must have values`() {
    val street = ""

    assertFailsWith(IllegalArgumentException::class) {
      Household(listOf(
          member.copy(firstName = "Apa", street = street),
          member.copy(firstName = "Bepa", street = street)
      ))
    }
  }

  @Test fun `Some addresses must have values`() {
    val address = ""

    assertFailsWith(IllegalArgumentException::class) {
      Household(listOf(
          member.copy(firstName = "Apa", address = address),
          member.copy(firstName = "Bepa", address = address)
      ))
    }
  }

  @Test fun `Has not paid`() {
    assertFalse(household.hasPaid())
  }

  @Test fun `Has correct householdNumber`() {
    assertEquals(1, household.getHouseholdNumber())
  }

  @Test fun `Finds membership type`() {
    assertEquals("Familj", household.getType())
  }

  @Test fun `Finds street`() {
    assertEquals("Apabepastraße 71", household.getStreet())
  }

  @Test fun `Finds address`() {
    assertEquals("123 City", household.getAddress())
  }

  @Test fun `Finds first e-mail`() {
    assertEquals("cepa@bepa.apa", household.getFirstEmail())
  }

  @Test fun `Finds first mobile phone`() {
    assertEquals("1234", household.getFirstMobile())
  }

  @Test fun `Finds no telephone`() {
    assertEquals("", household.getFirstTelephone())
  }

  @Test fun `Finds telephone`() {
    val fam = Household(listOf(member.copy(telephone = "71")))
    assertEquals("71", fam.getFirstTelephone())
  }

  @Test fun `Finds all first names`() {
    assertEquals(listOf("Apa", "Bepa", "Cepa", "Depa Epa"), household.getAllFirstNames())
  }

  @Test fun `Finds all surnames`() {
    assertEquals(listOf("Bepa", "Fepa", "Apa"), household.getAllSurnames())
  }

  @Test fun `Finds single surname`() {
    val fam = Household(listOf(
        member.copy(firstName = "Apa", surname = "Bepa"),
        member.copy(firstName = "Bepa", surname = "Bepa"),
        member.copy(firstName = "Cepa", surname = "Bepa"),
        member.copy(firstName = "Depa", surname = "Bepa"),
        member.copy(firstName = "Epa Fepa", surname = "Bepa")
    ))

    assertEquals(listOf("Bepa"), fam.getAllSurnames())
  }

  @Test fun `Household has several members`() {
    assertTrue(household.hasSeveralMembers())
  }

  @Test fun `Household has one member`() {
    val fam = Household(listOf(member.copy(type = "Hedersmedlem")))

    assertFalse(fam.hasSeveralMembers())
  }

  @Test fun `toString is pretty`() {
    assertEquals("4: Apa Bepa, Bepa Bepa, Cepa Fepa, Depa Epa Apa Bepa", household.toString())
  }
}
