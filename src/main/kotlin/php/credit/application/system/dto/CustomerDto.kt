package php.credit.application.system.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.br.CPF
import php.credit.application.system.entity.Address
import php.credit.application.system.entity.Customer
import java.math.BigDecimal

data class CustomerDto(
    @field:NotEmpty(message = "Please insert a First Name") 
    val firstName: String,
    @field:NotEmpty(message = "Please insert a Last Name")
    val lastName: String,
    @field:NotEmpty(message = "Please insert a CPF")
    @field:CPF(message = "Invalid CPF")
    val cpf: String,
    @field:NotEmpty(message = "Please insert an e-mail")
    @field:Email(message = "Invalid E-mail")
    val email: String,
    @field:NotNull(message = "Please insert an income")
    val income: BigDecimal,
    @field:NotEmpty(message = "Please insert a password")
    val password: String,
    @field:NotEmpty(message = "Please insert a zipCode")
    val zipCode: String,
    @field:NotEmpty(message = "Please insert a street")
    val street: String
) {

    fun toEntity(): Customer = Customer(
        firstName = this.firstName,
        lastName = this.lastName,
        cpf = this.cpf,
        email = this.email,
        income = this.income,
        password = this.password,
        address = Address(
            zipCode = this.zipCode,
            street = this.street
        )
    )
}
