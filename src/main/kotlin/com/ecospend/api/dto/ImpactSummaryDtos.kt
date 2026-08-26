package com.ecospend.api.dto

import com.ecospend.api.entity.ImpactTag
import java.math.BigDecimal

data class ImpactSummaryResponse(
    val userId: Long,
    val totalSpend: BigDecimal,
    val totalImpactScore: BigDecimal,
    val categoryBreakdown: List<CategoryImpactSummary>
)

data class CategoryImpactSummary(
    val categoryId: Long,
    val categoryName: String,
    val impactTag: ImpactTag,
    val totalSpend: BigDecimal,
    val impactWeight: BigDecimal,
    val impactScore: BigDecimal,
    val transactionCount: Int
)
