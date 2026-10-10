package com.example.myapplication

import com.example.myapplication.data.model.ExpenseItem
import com.example.myapplication.data.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ExpenseItemTest {

    @Test
    fun testToCsvRowAndFromCsvRow() {
        val original = ExpenseItem(
            date = "2026-10-10",
            time = "09:30:00",
            name = "ค่าอาหารเช้า",
            amount = 120.50,
            type = TransactionType.EXPENSE
        )

        val csvRow = original.toCsvRow()
        assertEquals("2026-10-10,09:30:00,ค่าอาหารเช้า,120.5,EXPENSE", csvRow)

        val parsed = ExpenseItem.fromCsvRow(csvRow)
        assertNotNull(parsed)
        assertEquals(original.date, parsed!!.date)
        assertEquals(original.time, parsed.time)
        assertEquals(original.name, parsed.name)
        assertEquals(original.amount, parsed.amount, 0.001)
        assertEquals(original.type, parsed.type)
    }

    @Test
    fun testInvalidCsvRowReturnsNull() {
        val invalidRow = "2026-10-10,09:30:00"
        val parsed = ExpenseItem.fromCsvRow(invalidRow)
        assertNull(parsed)
    }
}
