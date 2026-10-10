package com.example.myapplication.ui.stock

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.model.Product
import java.util.Locale

/**
 * Stateful Composable - รับผิดชอบเกี่ยวกับ ViewModel และ Side Effects
 */
@Composable
fun ProductStockScreen(
    viewModel: ProductStockViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    ProductStockContent(
        uiState = uiState,
        onUrlChanged = viewModel::onUrlChanged,
        onAnonKeyChanged = viewModel::onAnonKeyChanged,
        onNameChanged = viewModel::onNameChanged,
        onQtyChanged = viewModel::onQtyChanged,
        onPriceChanged = viewModel::onPriceChanged,
        onLoadClicked = viewModel::loadProducts,
        onAddClicked = viewModel::addProduct,
        onIncreaseClicked = viewModel::increaseStock,
        onDecreaseClicked = viewModel::decreaseStock,
        onDeleteClicked = viewModel::deleteProduct
    )
}

/**
 * Stateless Composable - แสดงผล UI ล้วนๆ ตามหลัก State Hoisting
 */
@Composable
fun ProductStockContent(
    uiState: ProductStockUiState,
    onUrlChanged: (String) -> Unit,
    onAnonKeyChanged: (String) -> Unit,
    onNameChanged: (String) -> Unit,
    onQtyChanged: (String) -> Unit,
    onPriceChanged: (String) -> Unit,
    onLoadClicked: () -> Unit,
    onAddClicked: () -> Unit,
    onIncreaseClicked: (Product) -> Unit,
    onDecreaseClicked: (Product) -> Unit,
    onDeleteClicked: (Long) -> Unit
) {
    var showConfigPanel by remember { mutableStateOf(uiState.supabaseUrl.isBlank() || uiState.supabaseAnonKey.isBlank()) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // หัวข้อหน้าจอ
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ระบบสต็อกสินค้าชุมชน (Supabase)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedButton(
                    onClick = { showConfigPanel = !showConfigPanel }
                ) {
                    Text(if (showConfigPanel) "ซ่อนตั้งค่า" else "ตั้งค่า Cloud")
                }
            }

            // 1. ส่วนตั้งค่า Supabase Connection
            AnimatedVisibility(visible = showConfigPanel) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "ตั้งค่าการเชื่อมต่อ Supabase Database",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = uiState.supabaseUrl,
                            onValueChange = onUrlChanged,
                            label = { Text("Supabase URL") },
                            placeholder = { Text("https://your-project.supabase.co") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = uiState.supabaseAnonKey,
                            onValueChange = onAnonKeyChanged,
                            label = { Text("Supabase Anon Key") },
                            placeholder = { Text("eyJhbGciOi...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Button(
                            onClick = onLoadClicked,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isLoading
                        ) {
                            Text("เชื่อมต่อและโหลดรายการสินค้า (Read)")
                        }
                    }
                }
            }

            // 2. ฟอร์มเพิ่มสินค้าใหม่ (Create)
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "เพิ่มรายการสินค้าใหม่ (Create)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = uiState.nameInput,
                        onValueChange = onNameChanged,
                        label = { Text("ชื่อสินค้า") },
                        placeholder = { Text("เช่น ข้าวฮักเมือง, ผ้าทอมือ") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.qtyInput,
                            onValueChange = onQtyChanged,
                            label = { Text("จำนวน (ชิ้น)") },
                            placeholder = { Text("0") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = uiState.priceInput,
                            onValueChange = onPriceChanged,
                            label = { Text("ราคา (บาท)") },
                            placeholder = { Text("0.00") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true
                        )
                    }

                    Button(
                        onClick = onAddClicked,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoading
                    ) {
                        Text("บันทึกเพิ่มสินค้าขึ้น Cloud")
                    }
                }
            }

            // Loading Bar
            AnimatedVisibility(visible = uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Text("กำลังสื่อสารกับ Supabase...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // 3. รายการสินค้าทั้งหมดในระบบ (Read, Update, Delete)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "คลังสินค้าชุมชน (${uiState.products.size} รายการ)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedButton(
                    onClick = onLoadClicked,
                    enabled = !uiState.isLoading
                ) {
                    Text("รีเฟรช")
                }
            }

            if (uiState.products.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ไม่พบรายการสินค้าในระบบ\n(กรุณากด 'ตั้งค่า Cloud' ใส่ URL/Key หรือกดเพิ่มสินค้า)",
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.products,
                        key = { it.id ?: it.productName }
                    ) { product ->
                        ProductStockCard(
                            product = product,
                            onIncreaseClicked = { onIncreaseClicked(product) },
                            onDecreaseClicked = { onDecreaseClicked(product) },
                            onDeleteClicked = { product.id?.let { id -> onDeleteClicked(id) } }
                        )
                    }
                }
            }
        }
    }
}

/**
 * การ์ดแสดงสินค้าแต่ละรายการ พร้อมปุ่มปรับสต็อก (+1/-1) และปุ่มลบ
 */
@Composable
private fun ProductStockCard(
    product: Product,
    onIncreaseClicked: () -> Unit,
    onDecreaseClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.productName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ID: ${product.id ?: "-"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Text(
                    text = String.format(Locale.getDefault(), "%,.2f ฿", product.price),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "คงเหลือ:", style = MaterialTheme.typography.bodyMedium)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "${product.stockQty} ชิ้น",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 14.sp
                        )
                    }
                }

                // ปุ่มปรับแต่งสต็อกและลบ
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDecreaseClicked,
                        enabled = product.stockQty > 0
                    ) {
                        Text("-1")
                    }

                    Button(
                        onClick = onIncreaseClicked
                    ) {
                        Text("+1")
                    }

                    OutlinedButton(
                        onClick = onDeleteClicked,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("ลบ")
                    }
                }
            }
        }
    }
}
