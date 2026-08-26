package com.ecospend.api.exception

class CategoryNotFoundException(categoryId: Long) : RuntimeException("Category with id $categoryId not found")
