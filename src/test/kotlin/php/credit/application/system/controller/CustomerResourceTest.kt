package php.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import php.credit.application.system.dto.CustomerDto
import php.credit.application.system.dto.CustomerUpdateDto
import php.credit.application.system.repository.CustomerRepository
import java.math.BigDecimal

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class CustomerResourceTest {

    @Autowired private lateinit var customerRepository: CustomerRepository
    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/customers"
    }

    @BeforeEach fun setup() = customerRepository.deleteAll()
    @AfterEach fun tearDown() = customerRepository.deleteAll()

    @Test
    @Order(1)
    fun `should Create Customer And Return 201 Status`() {
        //given
        val customerDto = buildCustomerDto()
        val valueAsString = objectMapper.writeValueAsString(customerDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString)
        ).andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Pedro Henrique"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("de Paula"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("06868437110"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("pedrohpaula98@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("1200.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("79041080"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua das Rosas"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should Not Save Customer With Same Cpf And Return 409 Status`() {
        //given
        customerRepository.save(buildCustomerDto().toEntity())
        val customerDto = buildCustomerDto()
        val valueAsString = objectMapper.writeValueAsString(customerDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        ).andExpect(MockMvcResultMatchers.status().isConflict)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Conflict! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(409))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.dao.DataIntegrityViolationException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should Not Save Customer With Empty First Name And Return 400 Status`() {
        //given
        val customerDto = buildCustomerDto(firstName = "")
        val valueAsString = objectMapper.writeValueAsString(customerDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Check the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.web.bind.MethodArgumentNotValidException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should Find Customer By Id And Return 200 Status`() {
        //given
        val customer = customerRepository.save(buildCustomerDto().toEntity())
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("$URL/${customer.id}")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Pedro Henrique"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("de Paula"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("06868437110"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("pedrohpaula98@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("1200.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("79041080"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua das Rosas"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(customer.id))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should Not Find Customer With Invalid Id And Return 400 Status`() {
        //given
        val invalidId: Long = -1L
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("$URL/$invalidId")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Check the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class php.credit.application.system.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should Delete Customer By Id And Return 204 Status`() {
        //given
        val customer = customerRepository.save(buildCustomerDto().toEntity())
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders
                .delete("$URL/${customer.id}")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNoContent)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should Not Delete Customer With Invalid Id And Return 400 Status`() {
        //given
        val invalidId: Long = -1L
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders
                .delete("$URL/$invalidId")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Check the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class php.credit.application.system.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should Update Customer And Return 200 Status`() {
        //given
        val customer = customerRepository.save(buildCustomerDto().toEntity())
        val customerUpdateDto = buildCustomerUpdateDto()
        val valueAsString = objectMapper.writeValueAsString(customerUpdateDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.patch("$URL?customerId=${customer.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("PedroUpdate"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("PaulaUpdate"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("06868437110"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("pedrohpaula98@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("2000.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("45656"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua Updated"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(customer.id))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should Not Update Customer With Invalid Id And Return 400 Status`() {
        //given
        val invalidId: Long = -1L
        val customerUpdateDto = buildCustomerUpdateDto()
        val valueAsString = objectMapper.writeValueAsString(customerUpdateDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.patch("$URL?customerId=$invalidId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Check the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class php.credit.application.system.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    private fun buildCustomerDto(
        firstName: String = "Pedro Henrique",
        lastName: String = "de Paula",
        cpf: String = "06868437110",
        email: String = "pedrohpaula98@gmail.com",
        income: BigDecimal = BigDecimal.valueOf(1200.0),
        password: String = "123456",
        zipCode: String = "79041080",
        street: String = "Rua das Rosas",
    ) = CustomerDto(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        income = income,
        password = password,
        zipCode = zipCode,
        street = street
    )

    private fun buildCustomerUpdateDto(
        firstName: String = "PedroUpdate",
        lastName: String = "PaulaUpdate",
        income: BigDecimal = BigDecimal.valueOf(2000.0),
        zipCode: String = "45656",
        street: String = "Rua Updated"
    ): CustomerUpdateDto = CustomerUpdateDto(
        firstName = firstName,
        lastName = lastName,
        income = income,
        zipCode = zipCode,
        street = street
    )
}