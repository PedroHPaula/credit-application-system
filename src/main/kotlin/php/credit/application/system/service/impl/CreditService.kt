package php.credit.application.system.service.impl

import org.springframework.stereotype.Service
import php.credit.application.system.entity.Credit
import php.credit.application.system.exception.BusinessException
import php.credit.application.system.repository.CreditRepository
import php.credit.application.system.service.ICreditService
import java.time.LocalDate
import java.util.*

@Service
class CreditService(
    private val creditRepository: CreditRepository,
    private val customerService: CustomerService
): ICreditService {
    override fun save(credit: Credit): Credit {
        this.validDayFirstInstallment(credit.dayFirstInstallment)
        credit.apply {
            customer = customerService.findById(credit.customer?.id!!)
        }
        return this.creditRepository.save(credit)
    }

    override fun findAllByCustomer(customerId: Long): List<Credit> =
        this.creditRepository.findAllByCustomer(customerId)

    override fun findByCreditCode(customerId: Long, creditCode: UUID): Credit {
        val credit = this.creditRepository.findByCreditCode(creditCode)
            ?: throw BusinessException("Credit Code $creditCode not found")
        return if (credit.customer?.id == customerId) credit else throw IllegalArgumentException("Contact the Admin")
    }

    private fun validDayFirstInstallment(dayFirstInstallment: LocalDate): Boolean {
        return if (dayFirstInstallment.isBefore(LocalDate.now().plusMonths(3))) true
        else throw BusinessException("Invalid Date - $dayFirstInstallment - for the First Installment")
    }
}