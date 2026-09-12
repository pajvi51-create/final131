package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
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
    private val lifecycleTag = "ToySpaceLifecycle"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showLifecycle("onCreate()")
        enableEdgeToEdge()
        setContent { MyApplicationTheme { ToySpaceApp() } }
    }

    override fun onStart() { super.onStart(); showLifecycle("onStart()") }
    override fun onResume() { super.onResume(); showLifecycle("onResume()") }
    override fun onPause() { showLifecycle("onPause()"); super.onPause() }
    override fun onStop() { showLifecycle("onStop()"); super.onStop() }
    override fun onDestroy() { showLifecycle("onDestroy()"); super.onDestroy() }

    private fun showLifecycle(name: String) {
        Log.d(lifecycleTag, name)
        Toast.makeText(applicationContext, name, Toast.LENGTH_SHORT).show()
    }
}

private data class Destination(val icon: String, val label: String)

@Composable
private fun ToySpaceApp() {
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val destinations = listOf(
        Destination("🚀", "ภารกิจ"),
        Destination("📍", "แผนที่"),
        Destination("🧮", "ค่างวด")
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 10.dp
            ) {
                destinations.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selected == index,
                        onClick = { selected = index },
                        icon = { Text(item.icon, style = MaterialTheme.typography.titleLarge) },
                        label = { Text(item.label, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (selected) {
                0 -> MissionScreen()
                1 -> MapScreen()
                else -> InstallmentScreen()
            }
        }
    }
}

@Composable
private fun PageHeader(kicker: String, title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(50)) {
            Text(kicker, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = MaterialTheme.colorScheme.onSecondaryContainer, style = MaterialTheme.typography.labelLarge)
        }
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ToyCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) { content() }
}

@Composable
private fun MissionScreen() {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Surface(Modifier.size(44.dp), color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(14.dp)) {
                Box(contentAlignment = Alignment.Center) { Text("★", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleLarge) }
            }
            Column {
                Text("TOY SPACE LAB", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                Text("พร้อมออกสำรวจ!", style = MaterialTheme.typography.titleLarge)
            }
        }

        Card(shape = RoundedCornerShape(32.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column {
                Image(
                    painter = painterResource(R.drawable.toy_space_hero),
                    contentDescription = "จรวดของเล่นและหมวกนักบินอวกาศ",
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    contentScale = ContentScale.Crop
                )
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("ภารกิจ Android", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                    Text("เรียนรู้ทุกวงโคจร", style = MaterialTheme.typography.headlineSmall)
                    Text("สำรวจ Lifecycle, พิกัด และการคำนวณ ผ่านห้องทดลองของเล่นอวกาศ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        PageHeader("MISSION 01", "วงโคจรของ Activity", "หมุนหน้าจอหรือสลับแอป แล้วสังเกต Toast และ Logcat")
        ToyCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Image(
                        painter = painterResource(R.drawable.student_photo),
                        contentDescription = "ภาพนักศึกษา",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(76.dp).clip(CircleShape)
                    )
                    Column {
                        Text("กฤษฎา ศรีคิลิน", style = MaterialTheme.typography.titleLarge)
                        Text("รหัส 67102122111", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.primaryContainer)
                val stages = listOf(
                    "01" to ("onCreate()" to "ประกอบยานและเตรียม UI"),
                    "02" to ("onStart()" to "ยานเริ่มปรากฏบนหน้าจอ"),
                    "03" to ("onResume()" to "พร้อมรับคำสั่งจากนักบิน"),
                    "04" to ("onPause()" to "เสียโฟกัสชั่วคราว"),
                    "05" to ("onStop()" to "ออกจากจอของผู้ใช้"),
                    "06" to ("onDestroy()" to "ถอดประกอบ Activity")
                )
                stages.forEach { (number, detail) -> LifecycleStage(number, detail.first, detail.second) }
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun LifecycleStage(number: String, method: String, description: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(Modifier.size(42.dp), color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(14.dp)) {
            Box(contentAlignment = Alignment.Center) { Text(number, color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.Black) }
        }
        Column(Modifier.weight(1f)) {
            Text(method, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MapScreen() {
    var latitude by rememberSaveable { mutableStateOf("") }
    var longitude by rememberSaveable { mutableStateOf("") }
    var mapLatitude by rememberSaveable { mutableStateOf("17.1899") }
    var mapLongitude by rememberSaveable { mutableStateOf("104.0914") }
    val context = LocalContext.current

    fun coordinates(): Pair<Double, Double>? {
        val lat = latitude.toDoubleOrNull()
        val lng = longitude.toDoubleOrNull()
        val error = when {
            lat == null || lng == null -> "กรอกพิกัดให้ครบทั้งสองช่อง"
            lat !in -90.0..90.0 || lng !in -180.0..180.0 -> "พิกัดอยู่นอกช่วงที่กำหนด"
            else -> null
        }
        if (error != null) Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        return if (error == null) lat!! to lng!! else null
    }

    fun openGoogleMaps(lat: Double, lng: Double) {
        val url = "https://www.google.com/maps/search/?api=1&query=$lat%2C$lng"
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        PageHeader("MISSION 02 • 📍", "ค้นหาดาวปลายทาง", "ระบุ Latitude และ Longitude เพื่อปักหมุดพิกัดที่ต้องการ")
        ToyCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("พิกัดปลายทาง", style = MaterialTheme.typography.titleLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CoordinateField(latitude, { latitude = it }, "Latitude", "17.1899", Modifier.weight(1f))
                    CoordinateField(longitude, { longitude = it }, "Longitude", "104.0914", Modifier.weight(1f))
                }
                Button(
                    onClick = { coordinates()?.let { (lat, lng) ->
                        mapLatitude = lat.toString()
                        mapLongitude = lng.toString()
                        openGoogleMaps(lat, lng)
                    } },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(17.dp)
                ) { Text("📍  ดูสถานที่ใน Google Maps") }
                Text("Latitude −90 ถึง 90  •  Longitude −180 ถึง 180", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), elevation = CardDefaults.cardElevation(5.dp)) {
            Column {
                Box(
                    Modifier.fillMaxWidth().height(250.dp).background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("🗺️", style = MaterialTheme.typography.displaySmall)
                        Text("Google Maps", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                        Text("เปิดดูชื่อสถานที่ ถนน และจุดสำคัญรอบพิกัด", color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("ดาวที่เลือก", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text("$mapLatitude, $mapLongitude", style = MaterialTheme.typography.titleLarge)
                    OutlinedButton(
                        onClick = {
                            openGoogleMaps(mapLatitude.toDouble(), mapLongitude.toDouble())
                        },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(17.dp)
                    ) { Text("เปิดสถานที่ใน Google Maps") }
                }
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun CoordinateField(value: String, onValueChange: (String) -> Unit, label: String, placeholder: String, modifier: Modifier) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label) }, placeholder = { Text(placeholder) },
        singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        shape = RoundedCornerShape(18.dp), modifier = modifier
    )
}

@Composable
private fun InstallmentScreen(vm: InstallmentViewModel = viewModel()) {
    val state = vm.uiState
    val context = LocalContext.current
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        PageHeader("MISSION 03 • 🧮", "วางแผนซื้อของเล่น", "คำนวณค่างวดแบบดอกเบี้ยคงที่ก่อนตัดสินใจ")
        ToyCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                OutlinedTextField(
                    value = state.price, onValueChange = vm::setPrice,
                    label = { Text("ราคาสินค้า") }, placeholder = { Text("เช่น 12,000") }, suffix = { Text("บาท") },
                    singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()
                )
                ValueControl("ดอกเบี้ยต่อเดือน", "${oneDecimal(state.monthlyRate)}%", "0%", "5%") {
                    Slider(state.monthlyRate, vm::setRate, valueRange = 0f..5f, steps = 49)
                }
                ValueControl("ระยะเวลาผ่อน", "${state.months} เดือน", "1 เดือน", "36 เดือน") {
                    Slider(state.months.toFloat(), { vm.setMonths(it.toInt()) }, valueRange = 1f..36f, steps = 34)
                }
                Button(
                    onClick = { if (!vm.calculate()) Toast.makeText(context, "กรอกราคาสินค้าให้ถูกต้อง", Toast.LENGTH_SHORT).show() },
                    modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary)
                ) { Text("★  คำนวณแผนการผ่อน") }
            }
        }

        state.result?.let { result ->
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("สรุปแผนของคุณ", style = MaterialTheme.typography.titleLarge)
                    Text("จ่ายต่อเดือน", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${money(result.monthlyPayment)} บาท", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                    HorizontalDivider()
                    ResultRow("ราคาสินค้า", "${money(state.price.toDoubleOrNull() ?: 0.0)} บาท")
                    ResultRow("ดอกเบี้ย", "${oneDecimal(state.monthlyRate)}% / เดือน")
                    ResultRow("จำนวนงวด", "${state.months} เดือน")
                    ResultRow("ดอกเบี้ยรวม", "${money(result.totalInterest)} บาท")
                    Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(18.dp)) {
                        ResultRow("ยอดชำระทั้งหมด", "${money(result.totalPayment)} บาท", Modifier.padding(16.dp))
                    }
                }
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun ValueControl(label: String, value: String, min: String, max: String, slider: @Composable () -> Unit) {
    Column {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.titleMedium)
            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(12.dp)) {
                Text(value, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
        slider()
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(min, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(max, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
    }
}

private fun oneDecimal(value: Float) = String.format(Locale.US, "%.1f", value)
private fun money(value: Double) = String.format(Locale.US, "%,.2f", value)
