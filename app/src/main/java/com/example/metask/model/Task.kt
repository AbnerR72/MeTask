package com.example.metask.model

import java.util.Date

data class Task (
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val bornDate: Date = Date(),
)
