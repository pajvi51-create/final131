package com.example.myapplication.data.repository

import android.content.Context
import com.example.myapplication.data.model.ExpenseItem
import java.io.File

/**
 * Implementation ของ ExpenseRepository ทำหน้าที่จัดการ File I/O
 * บนพื้นที่จัดเก็บข้อมูลภายในเครื่อง (Internal Storage: context.filesDir)
 */
class ExpenseRepositoryImpl(
    private val context: Context,
    private val fileName: String = "transactions.csv"
) : ExpenseRepository {

    private fun getFile(): File {
        return File(context.filesDir, fileName)
    }

    override suspend fun saveItem(item: ExpenseItem) {
        val file = getFile()
        val row = item.toCsvRow() + "\n"
        file.appendText(row)
    }

    override suspend fun getAllItems(): List<ExpenseItem> {
        val file = getFile()
        if (!file.exists()) return emptyList()

        return file.useLines { lines ->
            lines.mapNotNull { line ->
                if (line.isBlank()) null else ExpenseItem.fromCsvRow(line)
            }.toList()
        }
    }

    override suspend fun clearAllData(): Boolean {
        val file = getFile()
        return if (file.exists()) {
            file.delete()
        } else {
            true
        }
    }
}
