package com.example.myapplication.ui.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Product
import com.example.myapplication.data.repository.ProductRepository
import com.example.myapplication.data.repository.ProductRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * UI State สำหรับระบบจัดการสต็อกสินค้าชุมชนบน Supabase
 */
data class ProductStockUiState(
    val supabaseUrl: String = "",
    val supabaseAnonKey: String = "",
    val nameInput: String = "",
    val qtyInput: String = "",
    val priceInput: String = "",
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

/**
 * ViewModel สำหรับจัดการงาน CRUD บน Supabase Cloud Database
 */
class ProductStockViewModel(
    private val repository: ProductRepository = ProductRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductStockUiState())
    val uiState: StateFlow<ProductStockUiState> = _uiState.asStateFlow()

    fun onUrlChanged(newUrl: String) {
        _uiState.update { it.copy(supabaseUrl = newUrl, errorMessage = null) }
    }

    fun onAnonKeyChanged(newKey: String) {
        _uiState.update { it.copy(supabaseAnonKey = newKey, errorMessage = null) }
    }

    fun onNameChanged(newName: String) {
        _uiState.update { it.copy(nameInput = newName, errorMessage = null) }
    }

    fun onQtyChanged(newQty: String) {
        if (newQty.isEmpty() || newQty.matches(Regex("^\\d+$"))) {
            _uiState.update { it.copy(qtyInput = newQty, errorMessage = null) }
        }
    }

    fun onPriceChanged(newPrice: String) {
        if (newPrice.isEmpty() || newPrice.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { it.copy(priceInput = newPrice, errorMessage = null) }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    /**
     * ดึงข้อมูลรายการสินค้าทั้งหมดจาก Supabase (Read)
     */
    fun loadProducts() {
        val url = _uiState.value.supabaseUrl
        val key = _uiState.value.supabaseAnonKey

        if (url.isBlank() || key.isBlank()) {
            _uiState.update { it.copy(errorMessage = "กรุณากรอก Supabase URL และ Anon Key") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = withContext(Dispatchers.IO) {
                repository.getProducts(url, key)
            }
            result.fold(
                onSuccess = { list ->
                    _uiState.update {
                        it.copy(
                            products = list,
                            isLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "โหลดข้อมูลล้มเหลว: ${error.localizedMessage}"
                        )
                    }
                }
            )
        }
    }

    /**
     * เพิ่มสินค้าใหม่ขึ้นตาราง products บน Supabase (Create)
     */
    fun addProduct() {
        val state = _uiState.value
        val url = state.supabaseUrl
        val key = state.supabaseAnonKey

        if (url.isBlank() || key.isBlank()) {
            _uiState.update { it.copy(errorMessage = "กรุณากรอก Supabase URL และ Anon Key ก่อนกดบันทึก") }
            return
        }

        val name = state.nameInput.trim()
        val qty = state.qtyInput.trim().toIntOrNull()
        val price = state.priceInput.trim().toDoubleOrNull()

        if (name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "กรุณากรอกชื่อสินค้า") }
            return
        }
        if (qty == null || qty < 0) {
            _uiState.update { it.copy(errorMessage = "กรุณากรอกจำนวนสต็อกที่ถูกต้อง (>= 0)") }
            return
        }
        if (price == null || price < 0.0) {
            _uiState.update { it.copy(errorMessage = "กรุณากรอกราคาที่ถูกต้อง (>= 0.0)") }
            return
        }

        val newProduct = Product(
            productName = name,
            stockQty = qty,
            price = price
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = withContext(Dispatchers.IO) {
                repository.addProduct(url, key, newProduct)
            }
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            nameInput = "",
                            qtyInput = "",
                            priceInput = "",
                            successMessage = "เพิ่มสินค้าเรียบร้อยแล้ว"
                        )
                    }
                    loadProducts()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "เพิ่มสินค้าล้มเหลว: ${error.localizedMessage}"
                        )
                    }
                }
            )
        }
    }

    /**
     * ปรับสต็อกสินค้าขึ้น +1 (Update)
     */
    fun increaseStock(product: Product) {
        val id = product.id ?: return
        val newQty = product.stockQty + 1
        updateStockQty(id, newQty)
    }

    /**
     * ปรับสต็อกสินค้าลง -1 (Update - ขั้นต่ำ 0)
     */
    fun decreaseStock(product: Product) {
        val id = product.id ?: return
        if (product.stockQty <= 0) return
        val newQty = product.stockQty - 1
        updateStockQty(id, newQty)
    }

    private fun updateStockQty(id: Long, newQty: Int) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = withContext(Dispatchers.IO) {
                repository.updateStock(state.supabaseUrl, state.supabaseAnonKey, id, newQty)
            }
            result.fold(
                onSuccess = {
                    loadProducts()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "ปรับสต็อกล้มเหลว: ${error.localizedMessage}"
                        )
                    }
                }
            )
        }
    }

    /**
     * ลบสินค้าออกจาก Supabase (Delete)
     */
    fun deleteProduct(id: Long) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = withContext(Dispatchers.IO) {
                repository.deleteProduct(state.supabaseUrl, state.supabaseAnonKey, id)
            }
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(successMessage = "ลบสินค้าเรียบร้อยแล้ว") }
                    loadProducts()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "ลบสินค้าล้มเหลว: ${error.localizedMessage}"
                        )
                    }
                }
            )
        }
    }
}
