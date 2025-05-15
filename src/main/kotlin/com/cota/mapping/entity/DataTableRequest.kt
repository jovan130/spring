package com.cota.mapping.entity

data class DataTableRequest(
        val draw: Int,
        val start: Int,
        val length: Int,
        val search: Search?,
        val order: List<Order>?,
        val columns: List<Column>?
) {
    data class Search(val value: String)
    data class Order(val column: Int, val dir: String)
    data class Column(val data: String?, val name: String)
}
