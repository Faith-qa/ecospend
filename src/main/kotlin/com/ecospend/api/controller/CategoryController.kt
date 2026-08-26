package com.ecospend.api.controller

import com.ecospend.api.dto.CategoryResponse
import com.ecospend.api.dto.CreateCategoryRequest
import com.ecospend.api.service.CategoryService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/categories")
class CategoryController(
    private val categoryService: CategoryService
) {

    @PostMapping
    fun create(@Valid @RequestBody request: CreateCategoryRequest): ResponseEntity<CategoryResponse> {
        val created = categoryService.create(request)
        return ResponseEntity
            .created(URI.create("/api/categories/${created.id}"))
            .body(created)
    }

    @GetMapping
    fun findAll(): List<CategoryResponse> = categoryService.findAll()
}
