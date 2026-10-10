package com.example.myapplication.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ประเภทรายการ (รายรับ / รายจ่าย)
 */
enum class TransactionType(val displayName: String) {
    INCOME("รายรับ"),
    EXPENSE("รายจ่าย");

    companion object {
        fun fromString(value: String): TransactionType {
            return entries.firstOrNull {
                it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true)
            } ?: EXPENSE
        }
    }
}

/**
 * Data class สำหรับเก็บโครงสร้างข้อมูลแต่ละรายการบันทึกรายรับ-รายจ่าย
 * ได้แก่ วันที่, เวลา, ชื่อรายการ, จำนวนเงิน, และประเภทรายการ
 */
data class ExpenseItem(
    val date: String,
    val time: String,
    val name: String,
    val amount: Double,
    val type: TransactionType
) {
    /**
     * แปลง Object เป็นข้อความบรรทัด CSV
     * Format: date,time,name,amount,type
     */
    fun toCsvRow(): String {
        val sanitizedName = name.replace(",", " ")
        return "$date,$time,$sanitizedName,$amount,${type.name}"
    }

    companion object {
        /**
         * แปลงข้อความบรรทัด CSV กลับเป็น Object ExpenseItem
         */
        fun fromCsvRow(csvRow: String): ExpenseItem? {
            val parts = csvRow.split(",")
            if (parts.size < 5) return null

            return try {
                val date = parts[0].trim()
                val time = parts[1].trim()
                val name = parts[2].trim()
                val amount = parts[3].trim().toDoubleOrNull() ?: 0.0
                val type = TransactionType.fromString(parts[4].trim())

                ExpenseItem(
                    date = date,
                    time = time,
                    name = name,
                    amount = amount,
                    type = type
                )
            } catch (e: Exception) {
                null
            }
        }

        /**
         * สร้างวันที่และเวลาปัจจุบันสำหรับประทับเวลาการบันทึก
         */
        fun createCurrentDateTimePair(): Pair<String, String> {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val now = Date()
            return Pair(dateFormat.format(now), timeFormat.format(now))
        }
    }
}
