package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val lifecycleTag = "MidtermLifecycle"

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

    override fun onResume() {
        super.onResume()
        showLifecycleToast("onResume()")
    }

    override fun onPause() {
        showLifecycleToast("onPause()")
        super.onPause()
    }

    override fun onStop() {
        showLifecycleToast("onStop()")
        super.onStop()
    }

    override fun onDestroy() {
        showLifecycleToast("onDestroy()")
        super.onDestroy()
    }

    private fun showLifecycleToast(methodName: String) {
        Log.d(lifecycleTag, methodName)
        Toast.makeText(applicationContext, methodName, Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun MidtermApp() {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Lifecycle", "แผนที่", "ค่างวด")

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding)) {
            Surface(color = MaterialTheme.colorScheme.primaryContainer) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp)) {
                    Text(
                        "ANDROID MODERN",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Midterm Workspace",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Lifecycle · State Hoisting · MVVM", style = MaterialTheme.typography.bodyMedium)
                }
            }
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
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
private fun ScreenTitle(number: String, title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth()) {
        Text(number, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun LifecycleScreen() {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenTitle("ข้อ 1", "Activity Lifecycle", "ติดตามวงจรชีวิตของหน้าจอผ่าน Toast และ Logcat")
        Spacer(Modifier.height(20.dp))
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.student_photo),
                    contentDescription = "ภาพถ่ายนักศึกษา",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(210.dp).clip(CircleShape)
                )
                Spacer(Modifier.height(18.dp))
                Text("กฤษฎา ศรีคิลิน", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("รหัสนักศึกษา 67102122111", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(18.dp))
                HorizontalDivider()
                Spacer(Modifier.height(18.dp))
                Text("ลองหมุนหน้าจอเพื่อดู Lifecycle ทำงาน", textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(16.dp)) {
                    Text(
                        "onCreate → onStart → onResume\nonPause → onStop → onDestroy",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/** Stateful composable: owns saveable input and map state. */
@Composable
private fun MapScreen() {
    var latitude by rememberSaveable { mutableStateOf("") }
    var longitude by rememberSaveable { mutableStateOf("") }
    var mapLatitude by rememberSaveable { mutableStateOf("17.1899") }
    var mapLongitude by rememberSaveable { mutableStateOf("104.0914") }
    val context = LocalContext.current

    fun validatedCoordinates(): Pair<Double, Double>? {
        val lat = latitude.toDoubleOrNull()
        val lng = longitude.toDoubleOrNull()
        val message = when {
            lat == null || lng == null -> "กรุณากรอก Latitude และ Longitude ให้ครบถ้วน"
            lat !in -90.0..90.0 || lng !in -180.0..180.0 -> "ค่าพิกัดไม่ถูกต้อง"
            else -> null
        }
        if (message != null) {
            Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT).show()
            return null
        }
        return lat!! to lng!!
    }

    CoordinateForm(
        latitude = latitude,
        longitude = longitude,
        mapLatitude = mapLatitude,
        mapLongitude = mapLongitude,
        onLatitudeChange = { latitude = it },
        onLongitudeChange = { longitude = it },
        onShowMap = {
            validatedCoordinates()?.let { (lat, lng) ->
                mapLatitude = lat.toString()
                mapLongitude = lng.toString()
            }
        },
        onOpenExternalMap = {
            validatedCoordinates()?.let { (lat, lng) ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:$lat,$lng?q=$lat,$lng"))
                if (intent.resolveActivity(context.packageManager) != null) context.startActivity(intent)
                else Toast.makeText(context.applicationContext, "ไม่พบแอปแผนที่ภายนอก", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

/** Stateless composable: renders values and sends events upward only. */
@Composable
private fun CoordinateForm(
    latitude: String,
    longitude: String,
    mapLatitude: String,
    mapLongitude: String,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onShowMap: () -> Unit,
    onOpenExternalMap: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenTitle("ข้อ 2", "ค้นหาพิกัด", "ปักหมุดและดูแผนที่ได้ภายในแอป")
        Spacer(Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = latitude,
                        onValueChange = onLatitudeChange,
                        label = { Text("Latitude") },
                        placeholder = { Text("17.1899") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = longitude,
                        onValueChange = onLongitudeChange,
                        label = { Text("Longitude") },
                        placeholder = { Text("104.0914") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Button(onClick = onShowMap, modifier = Modifier.fillMaxWidth()) { Text("แสดงตำแหน่งบนแผนที่") }
            }
        }
        Spacer(Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier.fillMaxWidth().height(310.dp).background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    EmbeddedMap(mapLatitude, mapLongitude)
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("ตำแหน่งปัจจุบัน", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$mapLatitude, $mapLongitude", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(onClick = onOpenExternalMap) { Text("เปิดภายนอก") }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun EmbeddedMap(latitude: String, longitude: String) {
    val lat = latitude.toDouble()
    val lng = longitude.toDouble()
    val html = """
        <!doctype html>
        <html>
          <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
            <link rel="stylesheet" href="leaflet/leaflet.css">
            <style>
              html,body{width:100%;height:100%;margin:0;background:#e8eef0}
              #map{position:fixed;inset:0;width:100vw;height:100vh}
            </style>
          </head>
          <body>
            <div id="map"></div>
            <script src="leaflet/leaflet.js"></script>
            <script>
              const position = [$lat, $lng];
              const mapElement = document.getElementById('map');
              mapElement.style.width = window.innerWidth + 'px';
              mapElement.style.height = window.innerHeight + 'px';
              const map = L.map('map', { zoomControl: true }).setView(position, 16);
              L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
                maxZoom: 19,
                attribution: '&copy; OpenStreetMap contributors'
              }).addTo(map);
              L.marker(position).addTo(map).bindPopup('$lat, $lng');
              setTimeout(() => map.invalidateSize(), 250);
            </script>
          </body>
        </html>
    """.trimIndent()
    val mapKey = "$lat,$lng"

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String?) {
                        view.postDelayed({
                            view.evaluateJavascript("map.invalidateSize(true); map.setView(position, 16)", null)
                        }, 750)
                    }
                }
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                tag = mapKey
                loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            if (webView.tag != mapKey) {
                webView.tag = mapKey
                webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null)
            }
        }
    )
}

/** View layer: all installment state and calculations are delegated to the ViewModel. */
@Composable
private fun InstallmentScreen(vm: InstallmentViewModel = viewModel()) {
    val state = vm.uiState
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenTitle("ข้อ 3", "เครื่องคำนวณค่างวด", "วางแผนยอดผ่อนแบบดอกเบี้ยคงที่ด้วย MVVM")
        Spacer(Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(Modifier.padding(18.dp)) {
                OutlinedTextField(
                    value = state.price,
                    onValueChange = vm::setPrice,
                    label = { Text("ราคาสินค้า") },
                    suffix = { Text("บาท") },
                    placeholder = { Text("เช่น 12000") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(20.dp))
                Text("อัตราดอกเบี้ยต่อเดือน", style = MaterialTheme.typography.labelLarge)
                Text("${formatOneDecimal(state.monthlyRate)}%", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Slider(value = state.monthlyRate, onValueChange = vm::setRate, valueRange = 0f..5f, steps = 49)
                Spacer(Modifier.height(8.dp))
                Text("ระยะเวลาผ่อน", style = MaterialTheme.typography.labelLarge)
                Text("${state.months} เดือน", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Slider(value = state.months.toFloat(), onValueChange = { vm.setMonths(it.toInt()) }, valueRange = 1f..36f, steps = 34)
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (!vm.calculate()) Toast.makeText(context.applicationContext, "กรุณากรอกราคาสินค้าให้ถูกต้อง", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("คำนวณค่างวด") }
            }
        }
        state.result?.let { result ->
            Spacer(Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("สรุปยอดชำระ", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("ค่างวดต่อเดือน", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${money(result.monthlyPayment)} บาท", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    HorizontalDivider()
                    ResultRow("ยอดรวมที่ต้องจ่าย", "${money(result.totalPayment)} บาท")
                    ResultRow("ดอกเบี้ยทั้งหมด", "${money(result.totalInterest)} บาท")
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

private fun formatOneDecimal(value: Float) = String.format(Locale.US, "%.1f", value)
private fun money(value: Double) = String.format(Locale.US, "%,.2f", value)
