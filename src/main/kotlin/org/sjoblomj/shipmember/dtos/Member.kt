package org.sjoblomj.shipmember.dtos

data class Member(val householdNo: Int, val firstName: String, val surname: String, val type: String, val street: String,
                  val address: String, val telephone: String, val mobile: String, val email: String, val hasPaid: String) {

  fun hasPaid() = hasPaid.toBoolean()
}
