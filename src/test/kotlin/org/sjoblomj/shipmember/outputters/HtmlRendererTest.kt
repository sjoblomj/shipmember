package org.sjoblomj.shipmember.outputters

import org.junit.Assert.assertTrue
import org.junit.Test
import org.sjoblomj.shipmember.dtos.Household
import org.sjoblomj.shipmember.dtos.Member
import kotlin.test.assertFalse

class HtmlRendererTest {

  private val testTemplate = "templates/test.html"

  private val member = Member(1, "Apa", "Bepa", "Familj", "Apabepastraße 71", "123 City", "71", "1234", "cepa@bepa.apa", "0")

  private val household = Household(listOf(
      member.copy(firstName = "Apa", street = "", address = "", telephone = "71", mobile = "", email = ""),
      member.copy(firstName = "Bepa"),
      member.copy(firstName = "Cepa", surname = "Fepa", mobile = "2345", email = ""),
      member.copy(firstName = "Depa Epa", surname = "Apa Bepa", mobile = "3456", email = "")
  ))

  @Test fun `Family -- All contact forms`() {
    val output = renderHtml(household, testTemplate)

    assertTrue(output.contains("Dear Bepa, Fepa, Apa (Apa, Bepa, Cepa, Depa Epa)"))
    assertTrue(output.contains("We are honoured to have you as members of type Familj!"))
    assertTrue(output.contains("plz pay &gt;:("))
    assertTrue(output.contains("We have registered that you live on Apabepastraße 71, 123 City"))
    assertTrue(output.contains("Telephone number: 71"))
    assertTrue(output.contains("Mobile phone number: 1234"))
    assertTrue(output.contains("Email: cepa@bepa.apa"))
    assertTrue(output.contains("You and all your little kids in your Familj can join us for 400 sek."))
  }

  @Test fun `Family -- Member table is correct`() {
    val output = renderHtml(household, testTemplate)

    assertTrue(output.contains("    <td>Apa</td>\n" +
        "    <td>Bepa</td>\n" +
        "    <td> </td>\n" +
        "    <td> </td>\n" +
        "    <td>71</td>\n" +
        "    <td> </td>\n" +
        "    <td> </td>\n"))

    assertTrue(output.contains("    <td>Bepa</td>\n" +
        "    <td>Bepa</td>\n" +
        "    <td>Apabepastraße 71</td>\n" +
        "    <td>123 City</td>\n" +
        "    <td>71</td>\n" +
        "    <td>1234</td>\n" +
        "    <td>cepa@bepa.apa</td>\n"))

    assertTrue(output.contains("    <td>Cepa</td>\n" +
        "    <td>Fepa</td>\n" +
        "    <td>Apabepastraße 71</td>\n" +
        "    <td>123 City</td>\n" +
        "    <td>71</td>\n" +
        "    <td>2345</td>\n" +
        "    <td> </td>\n"))

    assertTrue(output.contains("    <td>Depa Epa</td>\n" +
        "    <td>Apa Bepa</td>\n" +
        "    <td>Apabepastraße 71</td>\n" +
        "    <td>123 City</td>\n" +
        "    <td>71</td>\n" +
        "    <td>3456</td>\n" +
        "    <td> </td>\n"))
  }

  @Test fun `Member of type Hedermedlem`() {
    val output = renderHtml(Household(listOf(member.copy(type = "Hedersmedlem"))), testTemplate)

    assertTrue(output.contains("Beloved Apa Bepa"))
    assertTrue(output.contains("We are honoured to have you as members of type Hedersmedlem!"))
    assertTrue(output.contains("Thank you for all your work, you owe us nothing."))
  }

  @Test fun `Member of type Ungdom`() {
    val output = renderHtml(Household(listOf(member.copy(type = "Ungdom"))), testTemplate)

    assertTrue(output.contains("Beloved Apa Bepa"))
    assertTrue(output.contains("We are honoured to have you as members of type Ungdom!"))
    assertTrue(output.contains("much wow. ur such young! amaze. 100 sek plz."))
  }

  @Test fun `Member of type Student`() {
    val output = renderHtml(Household(listOf(member.copy(type = "Student"))), testTemplate)

    assertTrue(output.contains("Beloved Apa Bepa"))
    assertTrue(output.contains("We are honoured to have you as members of type Student!"))
    assertTrue(output.contains("Despite being a Student, you still need to pay up. 200 sek."))
  }

  @Test fun `Member of type Enskild medlem`() {
    val output = renderHtml(Household(listOf(member.copy(type = "Enskild medlem"))), testTemplate)

    assertTrue(output.contains("Beloved Apa Bepa"))
    assertTrue(output.contains("We are honoured to have you as members of type Enskild medlem!"))
    assertTrue(output.contains("Find yourself a family and pay us 300 sek."))
  }

  @Test fun `Member of type Juridisk person`() {
    val output = renderHtml(Household(listOf(member.copy(type = "Juridisk person"))), testTemplate)

    assertTrue(output.contains("Beloved Apa Bepa"))
    assertTrue(output.contains("We are honoured to have you as members of type Juridisk person!"))
    assertTrue(output.contains("A company like yours can afford to pay 500 sek."))
  }

  @Test fun `No e-mail`() {
    val output = renderHtml(Household(listOf(member.copy(email = ""))), testTemplate)

    assertTrue(output.contains("Email missing"))
  }

  @Test fun `No mobile`() {
    val output = renderHtml(Household(listOf(member.copy(mobile = ""))), testTemplate)

    assertTrue(output.contains("Telephone number: 71"))
    assertFalse(output.contains("Mobile phone number:"))
  }

  @Test fun `No telephone`() {
    val output = renderHtml(Household(listOf(member.copy(telephone = ""))), testTemplate)

    assertTrue(output.contains("Mobile phone number: 1234"))
    assertFalse(output.contains("Telephone number:"))
  }

  @Test fun `No telephone or mobile`() {
    val output = renderHtml(Household(listOf(member.copy(telephone = "", mobile = ""))), testTemplate)

    assertTrue(output.contains("Telephone missing"))
    assertFalse(output.contains("Mobile phone number:"))
    assertFalse(output.contains("Telephone number:"))
  }

  @Test fun `Has paid`() {
    val output = renderHtml(Household(listOf(member.copy(hasPaid = "true"))), testTemplate)

    assertTrue(output.contains("Thx for paying"))
    assertFalse(output.contains("plz pay &gt;:("))
  }
}
