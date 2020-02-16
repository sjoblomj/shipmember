package org.sjoblomj.shipmember.dtos

enum class MEMBERTYPES { ALL, WITH_EMAILS, WITHOUT_EMAILS }
enum class OUTPUTTYPES { PDF_AND_EMAIL, EMAIL_OVER_PDF, PDF_ONLY }

data class Arguments(val inputFile : String, val outputDirectory : String, val emailSubject: String = "", val onlyNonPayers : Boolean,
                     val wantedMembers : MEMBERTYPES, val outputType : OUTPUTTYPES, val householdNumbers: List<Int> = emptyList())
