package com.ecospend.api.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "transactions")
class Transaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @Column(nullable = false, length = 200)
    var merchant: String,

    @Column(nullable = false, precision = 12, scale = 2)
    var amount: BigDecimal,

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    var category: Category,

    @Column(name = "occurred_at", nullable = false)
    var occurredAt: Instant,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now()
)
