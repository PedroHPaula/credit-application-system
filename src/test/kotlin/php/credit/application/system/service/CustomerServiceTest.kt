package php.credit.application.system.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import php.credit.application.system.entity.Address
import php.credit.application.system.entity.Customer
import php.credit.application.system.exception.BusinessException
import php.credit.application.system.repository.CustomerRepository
import php.credit.application.system.service.impl.CustomerService
import java.math.BigDecimal
import java.util.*

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CustomerServiceTest {

    @MockK lateinit var customerRepository: CustomerRepository
    @InjectMockKs lateinit var customerService: CustomerService

    @Test
    fun `should Create Customer`() {
        //given
        val fakeCustomer = buildCustomer()
        every { customerRepository.save(any()) } returns fakeCustomer
        //when
        val savedCustomer = customerService.save(fakeCustomer)
        //then
        Assertions.assertNotNull(savedCustomer)
        Assertions.assertEquals(fakeCustomer, savedCustomer)
        verify(exactly = 1) { customerRepository.save(fakeCustomer) }
    }

    @Test
    fun `should Find Customer By Id`() {
        //given
        val fakeId: Long = Random().nextLong()
        val fakeCustomer = buildCustomer(id = fakeId)
        every { customerRepository.findById(fakeId) } returns Optional.of(fakeCustomer)
        //when
        val customer = customerService.findById(fakeId)
        //then
        Assertions.assertNotNull(customer)
        Assertions.assertInstanceOf(Customer::class.java, customer)
        Assertions.assertEquals(customer, fakeCustomer)
        verify(exactly = 1) { customerRepository.findById(fakeId) }
    }

    @Test
    fun `should Not Find Customer By Invalid Id And Throw Business Exception`() {
        //given
        val fakeId: Long = Random().nextLong()
        every { customerRepository.findById(fakeId) } returns Optional.empty()
        //when
        //then
        Assertions.assertThrowsExactly(
            BusinessException::class.java,
            { customerService.findById(fakeId) },
            "Id $fakeId not found"
        )
    }

    @Test
    fun `should Delete Customer By Id`() {
        //given
        val fakeId: Long = Random().nextLong()
        val fakeCustomer = buildCustomer(id = fakeId)
        every { customerRepository.findById(fakeId) } returns Optional.of(fakeCustomer)
        every { customerRepository.delete(fakeCustomer) } just runs
        //when
        customerService.deleteById(fakeId)
        //then
        verify(exactly = 1) { customerRepository.findById(fakeId) }
        verify(exactly = 1) { customerRepository.delete(fakeCustomer) }
    }

    private fun buildCustomer(
        firstName: String = "Pedro Henrique",
        lastName: String = "de Paula",
        cpf: String = "06868437110",
        email: String = "pedrohpaula98@gmail.com",
        income: BigDecimal = BigDecimal.valueOf(1200.0),
        password: String = "123456",
        zipCode: String = "79041080",
        street: String = "Rua das Rosas",
        id: Long = 1L
    ) = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        income = income,
        password = password,
        address = Address(
            zipCode = zipCode,
            street = street
        ),
        id = id
    )
}