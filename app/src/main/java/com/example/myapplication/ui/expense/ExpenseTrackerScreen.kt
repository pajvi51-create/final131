package com.example.myapplication.ui.expense

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.model.ExpenseItem
import com.example.myapplication.data.model.TransactionType
import java.util.Locale

/**
 * Stateful Composable - รับผิดชอบเกี่ยวกับ ViewModel และ Side Effects
 */
@Composable
fun ExpenseTrackerScreen(
    viewModel: ExpenseViewModel = viewModel()
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

    ExpenseTrackerContent(
        uiState = uiState,
        onNameChanged = viewModel::onNameChanged,
        onAmountChanged = viewModel::onAmountChanged,
        onTypeChanged = viewModel::onTypeChanged,
        onSaveClicked = viewModel::saveTransaction,
        onClearClicked = viewModel::clearAllData
    )
}

/**
 * Stateless Composable - แสดงผล UI ล้วนๆ พร้อมรองรับ Adaptive Landscape
 */
@Composable
fun ExpenseTrackerContent(
    uiState: ExpenseUiState,
    onNameChanged: (String) -> Unit,
    onAmountChanged: (String) -> Unit,
    onTypeChanged: (TransactionType) -> Unit,
    onSaveClicked: () -> Unit,
    onClearClicked: () -> Unit
) {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isLandscape) {
            // Layout สองคอลัมน์สำหรับแนวนอน (Landscape)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ฝั่งซ้าย: สรุปยอด และ ฟอร์มกรอกข้อมูล (Scrollable)
                Column(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "ระบบบันทึกรายรับ-รายจ่าย (.CSV)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    NetBalanceCard(
                        totalIncome = uiState.totalIncome,
                        totalExpense = uiState.totalExpense,
                        netBalance = uiState.netBalance
                    )

                    ExpenseInputForm(
                        uiState = uiState,
                        onNameChanged = onNameChanged,
                        onAmountChanged = onAmountChanged,
                        onTypeChanged = onTypeChanged,
                        onSaveClicked = onSaveClicked,
                        onClearClicked = onClearClicked
                    )
                }

                // ฝั่งขวา: รายการประวัติย้อนหลัง (LazyColumn)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ประวัติรายการ (${uiState.items.size} รายการ)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (uiState.items.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ยังไม่มีข้อมูลบันทึกในไฟล์ CSV",
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = uiState.items,
                                key = { "${it.date}_${it.time}_${it.name}_${it.amount}" }
                            ) { item ->
                                ExpenseItemCard(item = item)
                            }
                        }
                    }
                }
            }
        } else {
            // Layout สแตกแนวตั้งสำหรับแนวตั้ง (Portrait)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "ระบบบันทึกรายรับ-รายจ่าย (.CSV)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                NetBalanceCard(
                    totalIncome = uiState.totalIncome,
                    totalExpense = uiState.totalExpense,
                    netBalance = uiState.netBalance
                )

                ExpenseInputForm(
                    uiState = uiState,
                    onNameChanged = onNameChanged,
                    onAmountChanged = onAmountChanged,
                    onTypeChanged = onTypeChanged,
                    onSaveClicked = onSaveClicked,
                    onClearClicked = onClearClicked
                )

                AnimatedVisibility(visible = uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                    }
                }

                Text(
                    text = "ประวัติรายการ (${uiState.items.size} รายการ)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (uiState.items.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ยังไม่มีข้อมูลบันทึกในไฟล์ CSV",
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = uiState.items,
                            key = { "${it.date}_${it.time}_${it.name}_${it.amount}" }
                        ) { item ->
                            ExpenseItemCard(item = item)
                        }
                    }
                }
            }
        }
    }
}

/**
 * ฟอร์มป้อนข้อมูลรายรับ-รายจ่าย
 */
@Composable
private fun ExpenseInputForm(
    uiState: ExpenseUiState,
    onNameChanged: (String) -> Unit,
    onAmountChanged: (String) -> Unit,
    onTypeChanged: (TransactionType) -> Unit,
    onSaveClicked: () -> Unit,
    onClearClicked: () -> Unit
) {
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
                text = "กรอกข้อมูลรายการใหม่",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = uiState.nameInput,
                onValueChange = onNameChanged,
                label = { Text("ชื่อรายการ") },
                placeholder = { Text("เช่น ค่าอาหาร, เงินเดือน") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.amountInput,
                onValueChange = onAmountChanged,
                label = { Text("จำนวนเงิน (บาท)") },
                placeholder = { Text("0.00") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "ประเภท:",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(end = 12.dp)
                )

                TransactionType.entries.forEach { type ->
                    Row(
                        modifier = Modifier
                            .selectable(
                                selected = (uiState.typeInput == type),
                                onClick = { onTypeChanged(type) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (uiState.typeInput == type),
                            onClick = null
                        )
                        Text(
                            text = type.displayName,
                            modifier = Modifier.padding(start = 2.dp),
                            color = if (type == TransactionType.INCOME) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onSaveClicked,
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isLoading
                ) {
                    Text("บันทึกข้อมูล")
                }

                OutlinedButton(
                    onClick = onClearClicked,
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("ล้างข้อมูล")
                }
            }
        }
    }
}

/**
 * การ์ดแสดงยอดเงินคงเหลือสุทธิ (Net Balance)
 */
@Composable
private fun NetBalanceCard(
    totalIncome: Double,
    totalExpense: Double,
    netBalance: Double
) {
    val netColor = when {
        netBalance > 0 -> Color(0xFF2E7D32)
        netBalance < 0 -> Color(0xFFC62828)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "ยอดเงินคงเหลือสุทธิ",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = String.format(Locale.getDefault(), "%,.2f บาท", netBalance),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = netColor
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "รายรับทั้งหมด", style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = String.format(Locale.getDefault(), "+%,.2f ฿", totalIncome),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "รายจ่ายทั้งหมด", style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = String.format(Locale.getDefault(), "-%,.2f ฿", totalExpense),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC62828)
                    )
                }
            }
        }
    }
}

/**
 * การ์ดแสดงรายการแต่ละแถวใน LazyColumn
 */
@Composable
private fun ExpenseItemCard(item: ExpenseItem) {
    val isIncome = item.type == TransactionType.INCOME
    val badgeColor = if (isIncome) Color(0xFF2E7D32) else Color(0xFFC62828)
    val sign = if (isIncome) "+" else "-"

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${item.date} | ${item.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = badgeColor.copy(alpha = 0.12f),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = String.format(Locale.getDefault(), "%s%,.2f ฿", sign, item.amount),
                    color = badgeColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 15.sp
                )
            }
        }
    }
}
