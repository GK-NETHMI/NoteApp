package com.example.noteapp

data class Note (val id: Int, val title:String, val content: String,var isPinned: Boolean=false)