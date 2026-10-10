package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Product

/**
 * Interface สำหรับจัดการข้อมูลสต็อกสินค้าบน Supabase Cloud Database (Repository Pattern)
 */
interface ProductRepository {
    suspend fun getProducts(url: String, anonKey: String): Result<List<Product>>
    suspend fun addProduct(url: String, anonKey: String, product: Product): Result<Unit>
    suspend fun updateStock(url: String, anonKey: String, id: Long, newStockQty: Int): Result<Unit>
    suspend fun deleteProduct(url: String, anonKey: String, id: Long): Result<Unit>
}
