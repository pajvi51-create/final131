package com.example.myapplication.data.repository

import com.example.myapplication.data.model.Product
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.net.HttpURLConnection
import java.net.URI

/**
 * Implementation สำหรับการสื่อสารกับ Supabase REST API
 * โดยใช้ HttpURLConnection และ kotlinx.serialization
 */
class ProductRepositoryImpl : ProductRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private fun formatUrl(baseUrl: String): String {
        var cleanUrl = baseUrl.trim().removeSuffix("/")
        if (!cleanUrl.startsWith("http://") && !cleanUrl.startsWith("https://")) {
            cleanUrl = "https://$cleanUrl"
        }
        return "$cleanUrl/rest/v1/products"
    }

    override suspend fun getProducts(url: String, anonKey: String): Result<List<Product>> {
        return try {
            val endpointUrl = "${formatUrl(url)}?select=*&order=id.desc"
            val connection = (URI.create(endpointUrl).toURL().openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", anonKey)
                setRequestProperty("Authorization", "Bearer $anonKey")
                setRequestProperty("Accept", "application/json")
                connectTimeout = 10000
                readTimeout = 10000
            }

            val responseCode = connection.responseCode
            if (responseCode in 200..299) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val products: List<Product> = json.decodeFromString(responseText)
                Result.success(products)
            } else {
                val errorText = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $responseCode"
                Result.failure(Exception("HTTP $responseCode: $errorText"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addProduct(url: String, anonKey: String, product: Product): Result<Unit> {
        return try {
            val endpointUrl = formatUrl(url)
            val jsonBody = buildJsonObject {
                put("product_name", product.productName)
                put("stock_qty", product.stockQty)
                put("price", product.price)
            }.toString()

            val connection = (URI.create(endpointUrl).toURL().openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("apikey", anonKey)
                setRequestProperty("Authorization", "Bearer $anonKey")
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Prefer", "return=minimal")
                doOutput = true
                connectTimeout = 10000
                readTimeout = 10000
            }

            connection.outputStream.use { os ->
                os.write(jsonBody.toByteArray(Charsets.UTF_8))
            }

            val responseCode = connection.responseCode
            if (responseCode in 200..299) {
                Result.success(Unit)
            } else {
                val errorText = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $responseCode"
                Result.failure(Exception("HTTP $responseCode: $errorText"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateStock(
        url: String,
        anonKey: String,
        id: Long,
        newStockQty: Int
    ): Result<Unit> {
        return try {
            val endpointUrl = "${formatUrl(url)}?id=eq.$id"
            val jsonBody = buildJsonObject {
                put("stock_qty", newStockQty)
            }.toString()

            val connection = (URI.create(endpointUrl).toURL().openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("X-HTTP-Method-Override", "PATCH")
                setRequestProperty("apikey", anonKey)
                setRequestProperty("Authorization", "Bearer $anonKey")
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Prefer", "return=minimal")
                doOutput = true
                connectTimeout = 10000
                readTimeout = 10000
            }

            connection.outputStream.use { os ->
                os.write(jsonBody.toByteArray(Charsets.UTF_8))
            }

            val responseCode = connection.responseCode
            if (responseCode in 200..299) {
                Result.success(Unit)
            } else {
                val errorText = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $responseCode"
                Result.failure(Exception("HTTP $responseCode: $errorText"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProduct(url: String, anonKey: String, id: Long): Result<Unit> {
        return try {
            val endpointUrl = "${formatUrl(url)}?id=eq.$id"
            val connection = (URI.create(endpointUrl).toURL().openConnection() as HttpURLConnection).apply {
                requestMethod = "DELETE"
                setRequestProperty("apikey", anonKey)
                setRequestProperty("Authorization", "Bearer $anonKey")
                connectTimeout = 10000
                readTimeout = 10000
            }

            val responseCode = connection.responseCode
            if (responseCode in 200..299) {
                Result.success(Unit)
            } else {
                val errorText = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $responseCode"
                Result.failure(Exception("HTTP $responseCode: $errorText"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
