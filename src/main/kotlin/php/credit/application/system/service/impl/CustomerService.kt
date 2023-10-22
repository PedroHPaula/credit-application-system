package php.credit.application.system.service.impl

import org.springframework.stereotype.Service
import php.credit.application.system.entity.Customer
import php.credit.application.system.exception.BusinessException
import php.credit.application.system.repository.CustomerRepository
import php.credit.application.system.service.ICustomerService

@Service
class CustomerService(
    private val customerRepository: CustomerRepository
): ICustomerService {
    override fun save(customer: Customer): Customer =
        this.customerRepository.save(customer)

    override fun findById(id: Long): Customer = this.customerRepository.findById(id)
        .orElseThrow { throw BusinessException("Id $id not found") }

    override fun deleteById(id: Long) {
        val customer = this.findById(id)
        this.customerRepository.delete(customer)
    }
    //this.customerRepository.deleteById(id)

}