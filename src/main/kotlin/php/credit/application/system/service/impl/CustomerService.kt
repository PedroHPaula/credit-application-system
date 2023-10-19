package php.credit.application.system.service.impl

import org.springframework.stereotype.Service
import php.credit.application.system.entity.Customer
import php.credit.application.system.repository.CustomerRepository
import php.credit.application.system.service.ICustomerService

@Service
class CustomerService(
    private val customerRepository: CustomerRepository
): ICustomerService {
    override fun save(customer: Customer): Customer =
        this.customerRepository.save(customer)

    override fun findById(id: Long): Customer = this.customerRepository.findById(id).orElseThrow {
        throw RuntimeException("Id $id not found")
    }

    override fun deleteById(id: Long) = this.customerRepository.deleteById(id)

}