package com.comicverse.products.controllers

import com.comicverse.products.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/delete")
class ProductDelete {

    @DeleteMapping("/{slug}")
    suspend fun deleteProduct(@PathVariable slug: String): ResponseEntity<Any> {

        val cleanSlug = slug.trim().lowercase()
        if (cleanSlug.isBlank()) {
            return ResponseEntity.badRequest()
                .body(mapOf("error" to "Slug inválido"))
        }

        // 1. Verificar si existe usando decodeList (ya NO se usa execute)
        val found = SupabaseClient.client
            .from("Products")
            .select {
                filter { eq("slug", cleanSlug) }
            }
            .decodeList<ProductCheck>()

        if (found.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "Producto con slug '$cleanSlug' no existe"))
        }

        // 2. Eliminar (delete tampoco usa execute)
        val deleteResult = SupabaseClient.client
            .from("Products")
            .delete {
                filter { eq("slug", cleanSlug) }
                select() // para que devuelva el registro eliminado
            }

        return ResponseEntity.ok(
            mapOf(
                "success" to true,
                "message" to "Producto eliminado correctamente",
                "deleted" to deleteResult.data // JSON RAW del eliminado
            )
        )
    }
}

@Serializable
data class ProductCheck(
    val slug: String
)
