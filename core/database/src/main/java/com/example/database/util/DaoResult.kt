package com.example.database.util

sealed class DaoResult<out T> {
    data class Success<out T>(val data: T?) : DaoResult<T>()
    data class Error(val message: String) : DaoResult<Nothing>()
    data object Loading : DaoResult<Nothing>()
}