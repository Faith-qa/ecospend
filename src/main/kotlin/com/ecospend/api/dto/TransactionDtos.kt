package com.ecospend.api.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.Instant

data class CreateTransactionRequest(
    @field:NotNull
    val userId: Long,

    @field:NotBlank
    @field:Size(max = 200)
    val merchant: String,

    @field:NotNull
    @field:Positive
    val amount: BigDecimal,

    @field:NotNull
    val categoryId: Long,

    @field:NotNull
    val occurredAt: Instant
)

data class TransactionResponse(
    val id: Long,
    val userId: Long,
    val merchant: String,
    val amount: BigDecimal,
    val occurredAt: Instant,
    val createdAt: Instant,
    val category: CategoryResponse
)
