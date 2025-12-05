package com.comicverse.products.controllers

import com.comicverse.products.SupabaseClient
import com.comicverse.products.models.Product
import io.github.jan.supabase.postgrest.from
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class ProductsGet {

    @GetMapping
    suspend fun getProducts(): List<Product> {
        return SupabaseClient.client
            .from("Products")
            .select()
            .decodeList<Product>()
    }
}
