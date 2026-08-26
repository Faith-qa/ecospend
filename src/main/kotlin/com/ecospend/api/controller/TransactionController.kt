package com.ecospend.api.controller

import com.ecospend.api.dto.CreateTransactionRequest
import com.ecospend.api.dto.TransactionResponse
import com.ecospend.api.service.TransactionService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/transactions")
class TransactionController(
    private val transactionService: TransactionService
) {

    @PostMapping
    fun create(@Valid @RequestBody request: CreateTransactionRequest): ResponseEntity<TransactionResponse> {
        val created = transactionService.create(request)
        return ResponseEntity
            .created(URI.create("/api/transactions/${created.id}"))
            .body(created)
    }

    @GetMapping
    fun findByUserId(@RequestParam userId: Long): List<TransactionResponse> =
        transactionService.findByUserId(userId)
}
