package com.example.myapplication.ui.expense

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.ExpenseItem
import com.example.myapplication.data.model.TransactionType
import com.example.myapplication.data.repository.ExpenseRepository
import com.example.myapplication.data.repository.ExpenseRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * UI State สำหรับหน้าบันทึกรายรับ-รายจ่าย (.CSV)
 */
data class ExpenseUiState(
    val nameInput: String = "",
    val amountInput: String = "",
    val typeInput: TransactionType = TransactionType.EXPENSE,
    val items: List<ExpenseItem> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netBalance: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

/**
 * ViewModel สำหรับจัดการตรรกะทางธุรกิจ (Business Logic) ของระบบบันทึกรายรับ-รายจ่าย
 */
class ExpenseViewModel(
    application: Application,
    private val repository: ExpenseRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        ExpenseRepositoryImpl(application.applicationContext)
    )

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    fun onNameChanged(newName: String) {
        _uiState.update { it.copy(nameInput = newName, errorMessage = null) }
    }

    fun onAmountChanged(newAmount: String) {
        if (newAmount.isEmpty() || newAmount.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { it.copy(amountInput = newAmount, errorMessage = null) }
        }
    }

    fun onTypeChanged(newType: TransactionType) {
        _uiState.update { it.copy(typeInput = newType) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    /**
     * โหลดรายการทั้งหมดจากไฟล์ CSV ผ่าน Dispatchers.IO
     */
    fun loadTransactions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val itemList = withContext(Dispatchers.IO) {
                repository.getAllItems()
            }
            updateCalculatedState(itemList)
        }
    }

    /**
     * บันทึกรายการใหม่ลงในไฟล์ CSV
     */
    fun saveTransaction() {
        val currentState = _uiState.value
        val name = currentState.nameInput.trim()
        val amountStr = currentState.amountInput.trim()

        if (name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "กรุณากรอกชื่อรายการ") }
            return
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            _uiState.update { it.copy(errorMessage = "กรุณากรอกจำนวนเงินที่ถูกต้อง (> 0)") }
            return
        }

        val (currentDate, currentTime) = ExpenseItem.createCurrentDateTimePair()
        val newItem = ExpenseItem(
            date = currentDate,
            time = currentTime,
            name = name,
            amount = amount,
            type = currentState.typeInput
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            withContext(Dispatchers.IO) {
                repository.saveItem(newItem)
            }
            val updatedList = withContext(Dispatchers.IO) {
                repository.getAllItems()
            }
            _uiState.update {
                it.copy(
                    nameInput = "",
                    amountInput = "",
                    typeInput = TransactionType.EXPENSE,
                    successMessage = "บันทึกรายการเรียบร้อยแล้ว"
                )
            }
            updateCalculatedState(updatedList)
        }
    }

    /**
     * ลบไฟล์ CSV และเคลียร์ข้อมูลบนหน้าจอ
     */
    fun clearAllData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            withContext(Dispatchers.IO) {
                repository.clearAllData()
            }
            _uiState.update {
                ExpenseUiState(
                    successMessage = "ล้างข้อมูลทั้งหมดเรียบร้อยแล้ว"
                )
            }
        }
    }

    /**
     * คำนวณรายรับรวม รายจ่ายรวม และยอดคงเหลือสุทธิ
     */
    private fun updateCalculatedState(items: List<ExpenseItem>) {
        var incomeSum = 0.0
        var expenseSum = 0.0

        for (item in items) {
            if (item.type == TransactionType.INCOME) {
                incomeSum += item.amount
            } else {
                expenseSum += item.amount
            }
        }

        val netBalance = incomeSum - expenseSum

        _uiState.update {
            it.copy(
                items = items.reversed(),
                totalIncome = incomeSum,
                totalExpense = expenseSum,
                netBalance = netBalance,
                isLoading = false
            )
        }
    }
}
