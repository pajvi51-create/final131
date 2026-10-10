package com.example.myapplication.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Class สำหรับโครงสร้างข้อมูลสินค้าในตาราง products บน Supabase Cloud Database
 */
@Serializable
data class Product(
    val id: Long? = null,
    @SerialName("product_name")
    val productName: String,
    @SerialName("stock_qty")
    val stockQty: Int,
    val price: Double
)
