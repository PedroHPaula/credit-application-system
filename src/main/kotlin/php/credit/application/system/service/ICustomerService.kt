package php.credit.application.system.service

import php.credit.application.system.entity.Customer

interface ICustomerService {
    fun save(customer: Customer): Customer
    fun findById(id: Long): Customer
    fun deleteById(id: Long)
}