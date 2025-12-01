package com.comicverse.products.controllers

import com.comicverse.products.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products")
class ProductsGet {

    @GetMapping
    suspend fun getProducts(): List<Product> {
        return SupabaseClient.client
            .from("Products")
            .select()
            .decodeList<Product>()
    }
}

@Serializable
data class Product(
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
