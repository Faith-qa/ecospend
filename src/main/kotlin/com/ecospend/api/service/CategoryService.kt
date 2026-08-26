package com.ecospend.api.service

import com.ecospend.api.dto.CategoryResponse
import com.ecospend.api.dto.CreateCategoryRequest
import com.ecospend.api.entity.Category
import com.ecospend.api.repository.CategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository
) {

    fun create(request: CreateCategoryRequest): CategoryResponse {
        val category = Category(
            name = request.name,
            impactTag = request.impactTag,
            impactWeight = request.impactWeight
        )
        return categoryRepository.save(category).toResponse()
    }

    fun findAll(): List<CategoryResponse> =
        categoryRepository.findAll().map { it.toResponse() }

    private fun Category.toResponse(): CategoryResponse = CategoryResponse(
        id = requireNotNull(id) { "Persisted category must have an id" },
        name = name,
        impactTag = impactTag,
        impactWeight = impactWeight
    )
}
