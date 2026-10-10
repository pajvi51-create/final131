package com.example.myapplication.ui.common

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun InstructionScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val sqlScript = """
        -- คำสั่ง SQL สำหรับสร้างตาราง products ใน Supabase SQL Editor
        create table if not exists products (
          id bigint primary key generated always as identity,
          product_name text not null,
          stock_qty int not null default 0,
          price double precision not null default 0.0
        );
    """.trimIndent()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "คู่มือและข้อมูลสำหรับการสอบนำเสนอ",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // การ์ดคำสั่ง SQL สำหรับ Supabase
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "📜 คำสั่ง SQL สร้างตาราง (Supabase)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E1E1E),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = sqlScript,
                            color = Color(0xFFD4D4D4),
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("SQL Script", sqlScript)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "คัดลอก SQL Script เรียบร้อยแล้ว", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("คัดลอก SQL Script")
                    }
                }
            }

            // คำแนะนำข้อที่ 1
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "1️⃣ ข้อที่ 1: การสอบนำเสนอระบบ CSV",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text("• ข้อมูลถูกบันทึกลงในไฟล์ transactions.csv ภายใน context.filesDir (Internal Storage)")
                    Text("• ทดสอบบันทึกรายการ -> ดูประวัติการย้อนหลัง และยอดสุทธิคำนวณทันที")
                    Text("• ทดสอบหมุนหน้าจอ (Configuration Change) -> ข้อมูลและ State จะไม่สูญหาย")
                    Text("• ทดสอบกดปุ่ม 'ล้างข้อมูล' -> ไฟล์ CSV จะถูกลบจริง และ UI เคลียร์ค่า")
                }
            }

            // คำแนะนำข้อที่ 2
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "2️⃣ ข้อที่ 2: การสอบนำเสนอระบบ Supabase CRUD",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text("• นำ Supabase Project URL และ Anon Key มาวางในส่วน 'ตั้งค่า Cloud'")
                    Text("• กดปุ่ม 'เชื่อมต่อและโหลดรายการสินค้า' เพื่อดึงข้อมูล (Read)")
                    Text("• ทดสอบเพิ่มสินค้า (Create) -> ตารางบน Supabase จะมีข้อมูลใหม่เพิ่มทันที")
                    Text("• กดปุ่ม '+1' หรือ '-1' (Update) -> จำนวนสต็อกบน Supabase เปลี่ยนแบบ Real-time")
                    Text("• กดปุ่ม 'ลบ' (Delete) -> แถวข้อมูลบน Supabase จะถูกลบจริงตาม ID")
                }
            }
        }
    }
}
