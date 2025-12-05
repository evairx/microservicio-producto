package com.comicverse.products.controllers

import com.comicverse.products.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/add")
class ProductsAdd {

    @PostMapping
    suspend fun addProduct(@RequestBody product: ProductAddRequest): Any {

        // 1. Generar slug base
        val baseSlug = (product.slug ?: product.title)
            .lowercase()
            .replace(" ", "-")
            .replace("[^a-z0-9-]".toRegex(), "")

        // 2. Verificar si el slug ya existe
        val existing = SupabaseClient.client
            .from("Products")
            .select {
                filter { eq("slug", baseSlug) }
            }
            .decodeList<Product>()

        // 3. Si existe → enviar error 400
        if (existing.isNotEmpty()) {
            return ResponseEntity
                .badRequest()
                .body(
                    mapOf(
                        "error" to "El slug '$baseSlug' ya existe.",
                        "slug" to baseSlug
                    )
                )
        }

        // 4. Crear objeto final con slug confirmado
        val data = product.copy(slug = baseSlug)

        // 5. Insertar y retornar el producto creado
        val inserted = SupabaseClient.client
            .from("Products")
            .insert(data) {
                select() // ← Necesario para recibir datos de vuelta
            }
            .decodeSingle<Product>()

        return inserted
    }
}

// ------------------- DTO -------------------

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
data class ProductAddRequest(
    val title: String,
    val price: Int,
    val price_offer: Int? = null,
    val description: String? = null,
    val image: String? = null,
    val rating: Rating? = null,
    val stock: Int? = null,
    val slug: String? = null,
    val category: String? = null,
    val home: Boolean? = null
)
