package com.example.chooser

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform