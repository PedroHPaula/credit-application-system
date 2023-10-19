package php.credit.application.system.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import php.credit.application.system.entity.Credit

@Repository
interface CreditRepository: JpaRepository<Credit, Long>