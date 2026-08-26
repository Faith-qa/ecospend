package com.ecospend.api.service

import com.ecospend.api.dto.CategoryImpactSummary
import com.ecospend.api.dto.ImpactSummaryResponse
import com.ecospend.api.entity.Transaction
import com.ecospend.api.repository.TransactionRepository
import org.springframework.stereotype.Service
import java.math.RoundingMode

@Service
class ImpactSummaryService(
    private val transactionRepository: TransactionRepository
) {

    fun calculate(userId: Long): ImpactSummaryResponse {
        val transactions = transactionRepository.findByUserId(userId)

        val categoryBreakdown = transactions
            .groupBy { it.category.id }
            .values
            .map { it.toCategoryImpactSummary() }
            .sortedByDescending { it.impactScore }

        val totalSpend = transactions.sumOf { it.amount }.setScale(2, RoundingMode.HALF_UP)
        val totalImpactScore = categoryBreakdown.sumOf { it.impactScore }.setScale(2, RoundingMode.HALF_UP)

        return ImpactSummaryResponse(
            userId = userId,
            totalSpend = totalSpend,
            totalImpactScore = totalImpactScore,
            categoryBreakdown = categoryBreakdown
        )
    }

    private fun List<Transaction>.toCategoryImpactSummary(): CategoryImpactSummary {
        val category = first().category
        val categorySpend = sumOf { it.amount }
        val impactScore = (categorySpend * category.impactWeight).setScale(2, RoundingMode.HALF_UP)

        return CategoryImpactSummary(
            categoryId = requireNotNull(category.id) { "Persisted category must have an id" },
            categoryName = category.name,
            impactTag = category.impactTag,
            totalSpend = categorySpend,
            impactWeight = category.impactWeight,
            impactScore = impactScore,
            transactionCount = size
        )
    }
}
