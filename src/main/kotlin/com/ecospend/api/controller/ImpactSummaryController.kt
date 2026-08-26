package com.ecospend.api.controller

import com.ecospend.api.dto.ImpactSummaryResponse
import com.ecospend.api.service.ImpactSummaryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class ImpactSummaryController(
    private val impactSummaryService: ImpactSummaryService
) {

    @GetMapping("/{userId}/impact-summary")
    fun getImpactSummary(@PathVariable userId: Long): ImpactSummaryResponse =
        impactSummaryService.calculate(userId)
}
