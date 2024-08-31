package com.example.myapplication.Model

data class TaskResponse(
    val choices: List<Choice>,
    val created: Long,
    val id: String,
    val model: String,
    val `object`: String,
    val system_fingerprint: Any,
    val usage: Usage
)