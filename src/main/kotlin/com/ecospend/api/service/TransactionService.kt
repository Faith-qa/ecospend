package com.ecospend.api.service

import com.ecospend.api.dto.CategoryResponse
import com.ecospend.api.dto.CreateTransactionRequest
import com.ecospend.api.dto.TransactionResponse
import com.ecospend.api.entity.Category
import com.ecospend.api.entity.Transaction
import com.ecospend.api.exception.CategoryNotFoundException
import com.ecospend.api.repository.CategoryRepository
import com.ecospend.api.repository.TransactionRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) {

    fun create(request: CreateTransactionRequest): TransactionResponse {
        val category = categoryRepository.findByIdOrNull(request.categoryId)
            ?: throw CategoryNotFoundException(request.categoryId)

        val transaction = Transaction(
            userId = request.userId,
            merchant = request.merchant,
            amount = request.amount,
            category = category,
            occurredAt = request.occurredAt
        )
        return transactionRepository.save(transaction).toResponse()
    }

    fun findByUserId(userId: Long): List<TransactionResponse> =
        transactionRepository.findByUserId(userId).map { it.toResponse() }

    private fun Transaction.toResponse(): TransactionResponse = TransactionResponse(
        id = requireNotNull(id) { "Persisted transaction must have an id" },
        userId = userId,
        merchant = merchant,
        amount = amount,
        occurredAt = occurredAt,
        createdAt = createdAt,
        category = category.toResponse()
    )

    private fun Category.toResponse(): CategoryResponse = CategoryResponse(
        id = requireNotNull(id) { "Persisted category must have an id" },
        name = name,
        impactTag = impactTag,
        impactWeight = impactWeight
    )
}
