package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.myapplication.ui.common.InstructionScreen
import com.example.myapplication.ui.expense.ExpenseTrackerScreen
import com.example.myapplication.ui.stock.ProductStockScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainFinalExamApp()
            }
        }
    }
}

private data class ExamNavigationItem(
    val title: String,
    val icon: String
)

@Composable
fun MainFinalExamApp() {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val navItems = listOf(
        ExamNavigationItem("ข้อ 1 (.CSV)", "📄"),
        ExamNavigationItem("ข้อ 2 (Supabase)", "☁️"),
        ExamNavigationItem("คำแนะนำ & SQL", "📜")
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = (selectedTabIndex == index),
                        onClick = { selectedTabIndex = index },
                        icon = { Text(text = item.icon) },
                        label = { Text(text = item.title, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTabIndex) {
                0 -> ExpenseTrackerScreen()
                1 -> ProductStockScreen()
                2 -> InstructionScreen()
            }
        }
    }
}
