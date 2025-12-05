package com.comicverse.products.controllers

import com.comicverse.products.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/")
class ProductUpdate {

    @PutMapping("/{slug}")
    suspend fun updateProduct(
        @PathVariable slug: String,
        @RequestBody request: ProductUpdateRequest
    ): ResponseEntity<Any> {

        val cleanSlug = slug.trim().lowercase()
        if (cleanSlug.isBlank()) {
            return ResponseEntity.badRequest()
                .body(mapOf("error" to "Slug inválido"))
        }

        // 1. Actualizar el producto directamente
        try {
            val updated = SupabaseClient.client
                .from("Products")
                .update({
                    request.title?.let { set("title", it) }
                    request.price?.let { set("price", it) }
                    request.price_offer?.let { set("price_offer", it) }
                    request.image?.let { set("image", it) }
                    request.description?.let { set("description", it) }
                    request.rating?.let { set("rating", it) }
                    request.stock?.let { set("stock", it) }
                    request.category?.let { set("category", it) }
                    request.home?.let { set("home", it) }
                }) {
                    filter { eq("slug", cleanSlug) }
                    select()
                }
                .decodeList<Product>()
            
            // Si no se actualizó nada, el producto no existe
            if (updated.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mapOf("error" to "Producto con slug '$cleanSlug' no existe"))
            }

            return ResponseEntity.ok(
                mapOf(
                    "success" to true,
                    "message" to "Producto actualizado correctamente",
                    "product" to updated.first()
                )
            )

        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to (e.message ?: "Error al actualizar")))
        }
    }
}

@Serializable
data class ProductUpdateRequest(
    val title: String? = null,
    val price: Int? = null,
    val price_offer: Int? = null,
    val image: String? = null,
    val description: String? = null,
    val rating: Rating? = null,
    val stock: Int? = null,
    val category: String? = null,
    val home: Boolean? = null
)

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
