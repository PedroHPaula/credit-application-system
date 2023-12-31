package php.credit.application.system.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import php.credit.application.system.dto.CreditDto
import php.credit.application.system.dto.CreditView
import php.credit.application.system.dto.CreditViewList
import php.credit.application.system.entity.Credit
import php.credit.application.system.service.impl.CreditService
import java.util.*

@RestController
@RequestMapping("/api/credits")
class CreditResource(
    private val creditService: CreditService
) {

    @PostMapping
    fun saveCredit(
        @RequestBody
        @Valid
        creditDto: CreditDto
    ): ResponseEntity<String> {
        val savedCredit = this.creditService.save(creditDto.toEntity())
        return ResponseEntity.status(HttpStatus.CREATED)
            .body("Credit ${savedCredit.creditCode} - Customer ${savedCredit.customer?.email} has been saved")
    }

    @GetMapping
    fun findAllByCustomerId(
        @RequestParam(value = "customerId")
        customerId: Long
    ): ResponseEntity<List<CreditViewList>> {
        val creditViewList = this.creditService.findAllByCustomer(customerId)
            .map { credit: Credit -> CreditViewList(credit) }
        return ResponseEntity.status(HttpStatus.OK).body(creditViewList)
    }

    @GetMapping("/{creditCode}")
    fun findByCreditCode(
        @RequestParam(value = "customerId")
        customerId: Long,
        @PathVariable
        creditCode: UUID
    ): ResponseEntity<CreditView> {
        val credit = this.creditService.findByCreditCode(customerId, creditCode)
        val creditView = CreditView(credit)
        return ResponseEntity.status(HttpStatus.OK).body(creditView)
    }

}