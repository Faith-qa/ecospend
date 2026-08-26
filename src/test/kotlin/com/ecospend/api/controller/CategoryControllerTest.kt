package com.ecospend.api.controller

import com.ecospend.api.repository.CategoryRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CategoryControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var categoryRepository: CategoryRepository

    @Test
    fun `POST category succeeds and persists`() {
        val requestBody = """
            {
              "name": "Test Electronics",
              "impactTag": "MEDIUM",
              "impactWeight": 3.00
            }
        """.trimIndent()

        mockMvc.post("/api/categories") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }.andExpect {
            status { isCreated() }
            header { exists("Location") }
            jsonPath("$.id") { exists() }
            jsonPath("$.name") { value("Test Electronics") }
            jsonPath("$.impactTag") { value("MEDIUM") }
            jsonPath("$.impactWeight") { value(3.0) }
        }

        val saved = categoryRepository.findAll().find { it.name == "Test Electronics" }
        assertNotNull(saved, "Category should be persisted in the database")
        assertEquals("MEDIUM", saved.impactTag.name)
    }
}
