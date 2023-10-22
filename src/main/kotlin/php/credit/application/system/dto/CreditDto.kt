package php.credit.application.system.dto

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import php.credit.application.system.entity.Credit
import php.credit.application.system.entity.Customer
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDto(
    @field:NotNull(message = "Please insert a Credit Value")
    val creditValue: BigDecimal,
    @field:NotNull(message = "Please insert a date for the First Installment")
    @field:Future(message = "The date of the First Installment is not valid")
    val dayFirstInstallment: LocalDate,
    @field:NotNull(message = "Please insert the Number of Installments")
    val numberOfInstallments: Int,
    @field:NotNull(message = "Please provide the ID of a Customer")
    val customerId: Long
) {

    fun toEntity(): Credit = Credit(
        creditValue = this.creditValue,
        dayFirstInstallment = this.dayFirstInstallment,
        numberOfInstallments = this.numberOfInstallments,
        customer = Customer(id = this.customerId)
    )

}
