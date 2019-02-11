package org.sjoblomj.shipmember.parsers

import org.junit.Test
import java.util.Arrays.asList
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.fail

class MemberParserTests {

  @Test fun `Empty line returns null`() {
    assertNull(createMember(""))
  }

  @Test fun `Illegal line returns null`() {
    assertNull(createMember("Nonsense"))
  }

  @Test fun `Proper line works fine`() {
    val householdNo = "1"
    val firstName = "Ada"
    val surname = "Lovelace"
    val type = "hed"
    val street = "Streetname 71"
    val address = "123 City"
    val telephone = "1234"
    val mobile = "4321"
    val email = "ada@lovelace.com"
    val hasPaid = "1"

    val member = createMember("$householdNo\t$firstName\t$surname\t$type\t$street\t$address\t$telephone\t$mobile\t$email\t$hasPaid")
        ?: fail("Should have found a member")

    assertEquals(Integer.parseInt(householdNo), member.householdNo)
    assertEquals(firstName, member.firstName)
    assertEquals(surname, member.surname)
    assertEquals("Hedersmedlem", member.type)
    assertEquals(street, member.street)
    assertEquals(address, member.address)
    assertEquals(telephone, member.telephone)
    assertEquals(mobile, member.mobile)
    assertEquals(email, member.email)
    assertEquals("true", member.hasPaid)
  }

  @Test fun `Multiple emails work fine`() {
    val householdNo = "1"
    val firstName = "Ada"
    val surname = "Lovelace"
    val type = "hed"
    val street = "Streetname 71"
    val address = "123 City"
    val telephone = "1234"
    val mobile = "4321"
    val email = "ada@lovelace.com ; bepa@lovelace.com"
    val hasPaid = "1"

    val member = createMember("$householdNo\t$firstName\t$surname\t$type\t$street\t$address\t$telephone\t$mobile\t$email\t$hasPaid")
        ?: fail("Should have found a member")

    assertEquals(Integer.parseInt(householdNo), member.householdNo)
    assertEquals(firstName, member.firstName)
    assertEquals(surname, member.surname)
    assertEquals("Hedersmedlem", member.type)
    assertEquals(street, member.street)
    assertEquals(address, member.address)
    assertEquals(telephone, member.telephone)
    assertEquals(mobile, member.mobile)
    assertEquals("ada@lovelace.com", member.email)
    assertEquals("true", member.hasPaid)
  }

  @Test fun `Whitespace polluted line works fine and cleans up properties`() {
    val householdNo = " 2  "
    val firstName = " Ada   "
    val surname = " Lovelace  "
    val type = "  hed "
    val street = "  Streetname   71 "
    val address = "123 City "
    val telephone = " 1234  "
    val mobile = " 4321 "
    val email = " ada@lovelace.com "
    val hasPaid = "0"

    val member = createMember("$householdNo\t$firstName\t$surname\t$type\t$street\t$address\t$telephone\t$mobile\t$email\t$hasPaid")
        ?: fail("Should have found a member")

    assertEquals(2, member.householdNo)
    assertEquals("Ada", member.firstName)
    assertEquals("Lovelace", member.surname)
    assertEquals("Hedersmedlem", member.type)
    assertEquals("Streetname 71", member.street)
    assertEquals("123 City", member.address)
    assertEquals("1234", member.telephone)
    assertEquals("4321", member.mobile)
    assertEquals("ada@lovelace.com", member.email)
    assertEquals("false", member.hasPaid)
  }

  @Test fun `Translation of all member types`() {

    val list = asList(
        Pair("stu", "Student"),
        Pair("ung", "Ungdom"),
        Pair("hed", "Hedersmedlem"),
        Pair("f", "Familj"),
        Pair("fh", "Familj"),
        Pair("jur.per.", "Juridisk person"),
        Pair("", "Enskild medlem"),
        Pair("Illegal type", "Enskild medlem")
    )

    parseMemberType(list)
  }


  @Test fun `Parse file`() {
    val list = parseFile("src/test/resources/members.csv")

    assertEquals(5, list.size)
    assertEquals("Petter", list[0].members[0].firstName)
    assertEquals("Malin", list[0].members[1].firstName)
    assertEquals("Staffan", list[0].members[2].firstName)
    assertEquals("Nils", list[0].members[3].firstName)
    assertEquals("Lena", list[0].members[4].firstName)
    assertEquals("Stina", list[1].members[0].firstName)
    assertEquals("Stein-Bjarne", list[2].members[0].firstName)
    assertEquals("Sophia", list[3].members[0].firstName)
    assertEquals("Gubb-Gunnar", list[4].members[0].firstName)
  }


  private fun parseMemberType(pairs : List<Pair<String, String>>) {
    for (pair : Pair<String, String> in pairs) {
      val short = pair.first
      val long = pair.second

      val member = createMember("3\tAda\tLovelace\t$short\tStreetname 71\t123 City\t1234\t4321\tada@lovelace.com\t0")
          ?: fail("Should have found a member")

      assertEquals(long, member.type)
    }
  }
}
