package com.ecospend.api.dto

import com.ecospend.api.entity.ImpactTag
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class CreateCategoryRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val name: String,

    @field:NotNull
    val impactTag: ImpactTag,

    @field:NotNull
    @field:PositiveOrZero
    val impactWeight: BigDecimal
)

data class CategoryResponse(
    val id: Long,
    val name: String,
    val impactTag: ImpactTag,
    val impactWeight: BigDecimal
)
