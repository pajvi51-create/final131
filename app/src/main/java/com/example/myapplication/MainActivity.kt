package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showLifecycleToast("onCreate()")
        enableEdgeToEdge()
        setContent { MyApplicationTheme { MidtermApp() } }
    }

    override fun onStart() {
        super.onStart()
        showLifecycleToast("onStart()")
    }

    private fun showLifecycleToast(methodName: String) {
        Toast.makeText(applicationContext, methodName, Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun MidtermApp() {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Lifecycle", "แผนที่", "ค่างวด MVVM")

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding)) {
            Text(
                "ข้อสอบกลางภาค Android สมัยใหม่",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            when (selectedTab) {
                0 -> LifecycleScreen()
                1 -> MapScreen()
                else -> InstallmentScreen()
            }
        }
    }
}

@Composable
private fun LifecycleScreen() {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("ข้อ 1: Activity Lifecycle", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Image(
            painter = painterResource(R.drawable.student_photo),
            contentDescription = "ภาพถ่ายนักศึกษา",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(220.dp).clip(CircleShape)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "ทดลองเปิดแอป หมุนหน้าจอ หรือออกจากแอป แล้วสังเกต Toast ของแต่ละ Lifecycle",
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))
        Text(
            "onCreate() → onStart() → onResume()\nonPause() → onStop() → onDestroy()",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
    }
}

/** Stateful composable: owns saveable state and the map-launching logic. */
@Composable
private fun MapScreen() {
    var latitude by rememberSaveable { mutableStateOf("") }
    var longitude by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    CoordinateForm(
        latitude = latitude,
        longitude = longitude,
        onLatitudeChange = { latitude = it },
        onLongitudeChange = { longitude = it },
        onOpenMap = {
            val lat = latitude.toDoubleOrNull()
            val lng = longitude.toDoubleOrNull()
            if (lat == null || lng == null) {
                Toast.makeText(context.applicationContext, "กรุณากรอก Latitude และ Longitude ให้ครบถ้วน", Toast.LENGTH_SHORT).show()
                return@CoordinateForm
            }
            if (lat !in -90.0..90.0 || lng !in -180.0..180.0) {
                Toast.makeText(context.applicationContext, "ค่าพิกัดไม่ถูกต้อง", Toast.LENGTH_SHORT).show()
                return@CoordinateForm
            }
            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:$lat,$lng?q=$lat,$lng"))
            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
            } else {
                Toast.makeText(context.applicationContext, "ไม่พบแอปพลิเคชันแผนที่", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

/** Stateless composable: renders values and reports user events only. */
@Composable
private fun CoordinateForm(
    latitude: String,
    longitude: String,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onOpenMap: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "ข้อ 2: เปิดแผนที่ด้วย State Hoisting",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(
            value = latitude,
            onValueChange = onLatitudeChange,
            label = { Text("Latitude (ละติจูด)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = longitude,
            onValueChange = onLongitudeChange,
            label = { Text("Longitude (ลองจิจูด)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onOpenMap, modifier = Modifier.fillMaxWidth()) { Text("เปิดแผนที่") }
    }
}

/** View layer: state and calculations are delegated to the ViewModel. */
@Composable
private fun InstallmentScreen(vm: InstallmentViewModel = viewModel()) {
    val state = vm.uiState
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "ข้อ 3: คำนวณค่างวดแบบ MVVM",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = state.price,
            onValueChange = vm::setPrice,
            label = { Text("ราคาสินค้า (บาท)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Text("อัตราดอกเบี้ยต่อเดือน: ${formatOneDecimal(state.monthlyRate)}%")
        Slider(value = state.monthlyRate, onValueChange = vm::setRate, valueRange = 0f..5f, steps = 49)
        Text("จำนวนเดือนในการผ่อน: ${state.months} เดือน")
        Slider(
            value = state.months.toFloat(),
            onValueChange = { vm.setMonths(it.toInt()) },
            valueRange = 1f..36f,
            steps = 34
        )
        Button(
            onClick = {
                if (!vm.calculate()) {
                    Toast.makeText(context.applicationContext, "กรุณากรอกราคาสินค้าให้ถูกต้อง", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("คำนวณ") }
        Spacer(Modifier.height(16.dp))
        state.result?.let { result ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("ผลการคำนวณ", fontWeight = FontWeight.Bold)
                    Text("ค่างวดต่อเดือน: ${money(result.monthlyPayment)} บาท")
                    Text("ยอดรวมที่ต้องจ่ายจริง: ${money(result.totalPayment)} บาท")
                    Text("ยอดดอกเบี้ยทั้งหมด: ${money(result.totalInterest)} บาท")
                }
            }
        }
    }
}

private fun formatOneDecimal(value: Float) = String.format(Locale.US, "%.1f", value)
private fun money(value: Double) = String.format(Locale.US, "%,.2f", value)
