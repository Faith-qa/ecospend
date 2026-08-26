package com.ecospend.api.controller

import com.ecospend.api.entity.Transaction
import com.ecospend.api.repository.CategoryRepository
import com.ecospend.api.repository.TransactionRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ImpactSummaryControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var categoryRepository: CategoryRepository

    @Autowired
    lateinit var transactionRepository: TransactionRepository

    @Test
    fun `impact summary aggregates spend and score correctly across categories`() {
        val userId = 555L
        val flights = categoryRepository.findAll().first { it.name == "Flights" }
        val groceries = categoryRepository.findAll().first { it.name == "Groceries" }
        val publicTransport = categoryRepository.findAll().first { it.name == "Public Transport" }

        val transactions = listOf(
            Transaction(userId = userId, merchant = "Kenya Airways", amount = BigDecimal("2000.00"), category = flights, occurredAt = Instant.parse("2026-08-01T09:00:00Z")),
            Transaction(userId = userId, merchant = "Qatar Airways", amount = BigDecimal("3000.00"), category = flights, occurredAt = Instant.parse("2026-08-02T09:00:00Z")),
            Transaction(userId = userId, merchant = "Naivas", amount = BigDecimal("1000.00"), category = groceries, occurredAt = Instant.parse("2026-08-03T09:00:00Z")),
            Transaction(userId = userId, merchant = "Carrefour", amount = BigDecimal("500.00"), category = groceries, occurredAt = Instant.parse("2026-08-04T09:00:00Z")),
            Transaction(userId = userId, merchant = "Chandarana", amount = BigDecimal("500.00"), category = groceries, occurredAt = Instant.parse("2026-08-05T09:00:00Z")),
            Transaction(userId = userId, merchant = "Matatu", amount = BigDecimal("100.00"), category = publicTransport, occurredAt = Instant.parse("2026-08-06T09:00:00Z"))
        )
        transactionRepository.saveAll(transactions)

        mockMvc.get("/api/users/$userId/impact-summary")
            .andExpect {
                status { isOk() }
                jsonPath("$.totalSpend") { value(7100.0) }
                jsonPath("$.totalImpactScore") { value(29100.0) }
                jsonPath("$.categoryBreakdown.length()") { value(3) }

                jsonPath("$.categoryBreakdown[0].categoryName") { value("Flights") }
                jsonPath("$.categoryBreakdown[0].totalSpend") { value(5000.0) }
                jsonPath("$.categoryBreakdown[0].impactWeight") { value(5.0) }
                jsonPath("$.categoryBreakdown[0].impactScore") { value(25000.0) }
                jsonPath("$.categoryBreakdown[0].transactionCount") { value(2) }

                jsonPath("$.categoryBreakdown[1].categoryName") { value("Groceries") }
                jsonPath("$.categoryBreakdown[1].totalSpend") { value(2000.0) }
                jsonPath("$.categoryBreakdown[1].impactWeight") { value(2.0) }
                jsonPath("$.categoryBreakdown[1].impactScore") { value(4000.0) }
                jsonPath("$.categoryBreakdown[1].transactionCount") { value(3) }

                jsonPath("$.categoryBreakdown[2].categoryName") { value("Public Transport") }
                jsonPath("$.categoryBreakdown[2].totalSpend") { value(100.0) }
                jsonPath("$.categoryBreakdown[2].impactWeight") { value(1.0) }
                jsonPath("$.categoryBreakdown[2].impactScore") { value(100.0) }
                jsonPath("$.categoryBreakdown[2].transactionCount") { value(1) }
            }
    }
}
