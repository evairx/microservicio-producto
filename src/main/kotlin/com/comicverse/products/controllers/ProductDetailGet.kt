package com.comicverse.products.controllers

import com.comicverse.products.SupabaseClient
import io.github.jan.supabase.postgrest.from
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class ProductDetailGet {

    @GetMapping("/{slug}")
    suspend fun getProductBySlug(@PathVariable slug: String): ResponseEntity<Any> {

        return try {

            val list = SupabaseClient.client
                .from("Products")
                .select {
                    filter {
                        eq("slug", slug)
                    }
                }
                .decodeList<ProductDetail>()

            val product = list.firstOrNull()

            if (product == null) {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mapOf("error" to "Product not found"))
            } else {
                ResponseEntity.ok(product)
            }

        } catch (e: Exception) {
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to (e.message ?: "Unknown error")))
        }
    }
}

@Serializable
data class ProductDetail(
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
