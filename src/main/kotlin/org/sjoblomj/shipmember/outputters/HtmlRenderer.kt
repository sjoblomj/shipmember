package org.sjoblomj.shipmember.outputters

import org.sjoblomj.shipmember.dtos.Household
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.io.StringWriter
import java.util.*

private const val templateName = "templates/email.html"

fun renderHtml(household: Household): String {
  return renderHtml(household, templateName)
}

fun renderHtml(household: Household, templateName: String): String {
  val resolver = ClassLoaderTemplateResolver()
  resolver.templateMode = TemplateMode.HTML
  val engine = TemplateEngine()
  engine.setTemplateResolver(resolver)

  val writer = StringWriter()
  engine.process(templateName, setContextVariables(household), writer)

  return writer.toString()
}

private fun setContextVariables(household: Household): Context {
  val context = Context(Locale.getDefault())

  context.setVariable("members", household.members)
  context.setVariable("householdNumber", household.getHouseholdNumber())
  context.setVariable("hasSeveralMembers", household.hasSeveralMembers())
  context.setVariable("hasPaid", household.hasPaid())
  context.setVariable("firstNames", household.getAllFirstNames().joinToString(", "))
  context.setVariable("surnames", household.getAllSurnames().joinToString(", "))
  context.setVariable("type", household.getType())
  context.setVariable("street", household.getStreet())
  context.setVariable("address", household.getAddress())
  context.setVariable("telephone", household.getFirstTelephone())
  context.setVariable("mobile", household.getFirstMobile())
  context.setVariable("email", household.getFirstEmail())

  return context
}
