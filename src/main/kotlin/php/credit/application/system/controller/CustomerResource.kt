package php.credit.application.system.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import php.credit.application.system.dto.CustomerDto
import php.credit.application.system.dto.CustomerUpdateDto
import php.credit.application.system.dto.CustomerView
import php.credit.application.system.entity.Customer
import php.credit.application.system.service.impl.CustomerService

@RestController
@RequestMapping("/api/customers")
class CustomerResource(
    private val customerService: CustomerService
) {

    @PostMapping
    fun saveCustomer(
        @RequestBody
        @Valid
        customerDto: CustomerDto
    ): ResponseEntity<CustomerView> {
        val savedCustomer = this.customerService.save(customerDto.toEntity())
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(CustomerView(savedCustomer))
    }

    @GetMapping("/{customerId}")
    fun findCustomerById(
        @PathVariable
        customerId: Long
    ): ResponseEntity<CustomerView> {
        val customer: Customer = this.customerService.findById(customerId)
        return ResponseEntity.status(HttpStatus.OK)
            .body(CustomerView(customer))
    }

    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCustomerbyId(
        @PathVariable
        customerId: Long
    ) = this.customerService.deleteById(customerId)

    @PatchMapping
    fun updateCustomer(
        @RequestParam(value = "customerId")
        customerId: Long,
        @RequestBody
        @Valid
        customerUpdateDto: CustomerUpdateDto
    ): ResponseEntity<CustomerView> {
        val customer = this.customerService.findById(customerId)
        val customerToUpdate = customerUpdateDto.toEntity(customer)
        val customerUpdated = this.customerService.save(customerToUpdate)
        return ResponseEntity.status(HttpStatus.OK)
            .body(CustomerView(customerUpdated))
    }
}