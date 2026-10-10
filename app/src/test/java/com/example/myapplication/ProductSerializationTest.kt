package com.example.myapplication

import com.example.myapplication.data.model.Product
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductSerializationTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testProductSerializationAndDeserialization() {
        val jsonString = """{"id":10,"product_name":"ข้าวฮักเมือง","stock_qty":25,"price":150.0}"""

        val product = json.decodeFromString(Product.serializer(), jsonString)
        assertEquals(10L, product.id)
        assertEquals("ข้าวฮักเมือง", product.productName)
        assertEquals(25, product.stockQty)
        assertEquals(150.0, product.price, 0.001)

        val encoded = json.encodeToString(Product.serializer(), product)
        assertTrue(encoded.contains("\"product_name\":\"ข้าวฮักเมือง\""))
        assertTrue(encoded.contains("\"stock_qty\":25"))
    }
}
