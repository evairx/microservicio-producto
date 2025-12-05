package com.comicverse.products.models

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int? = null,
    val title: String? = null,
    val price: Int? = null,
    val price_offer: Int? = null,
    val image: String? = null,
    val description: String? = null,
    val rating: Rating? = null,
    val stock: Int? = null,
    val category: String? = null,
    val home: Boolean? = null,
    val slug: String? = null
)

@Serializable
data class Rating(
    val rate: Double? = null,
    val count: Int? = null
)
