package com.cota.mapping.entity

data class DataTableResponse<T>(
        val draw: Int,
        val recordsTotal: Long,
        val recordsFiltered: Long,
        val data: List<T>
)
