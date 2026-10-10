package com.example.myapplication.data.repository

import com.example.myapplication.data.model.ExpenseItem

/**
 * Interface สำหรับจัดการระบบไฟล์ CSV รายรับ-รายจ่าย (Repository Pattern)
 */
interface ExpenseRepository {
    suspend fun saveItem(item: ExpenseItem)
    suspend fun getAllItems(): List<ExpenseItem>
    suspend fun clearAllData(): Boolean
}
