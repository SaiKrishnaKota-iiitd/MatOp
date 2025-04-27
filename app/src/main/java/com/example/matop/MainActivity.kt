package com.example.matop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.matop.ui.theme.MatOpTheme

class MainActivity : ComponentActivity() {

    external fun addMatrices(matrixA: FloatArray, matrixB: FloatArray): FloatArray
    external fun subtractMatrices(matrixA: FloatArray, matrixB: FloatArray): FloatArray
    external fun multiplyMatrices(matrixA: FloatArray, matrixB: FloatArray, n: Int, m: Int, p: Int): FloatArray
    external fun divideMatrices(matrixA: FloatArray, matrixB: FloatArray): FloatArray

    companion object {
        init {
            System.loadLibrary("matop")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MatOpTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MatrixCalculatorScreen()
                }
            }
        }
    }
}

@Composable
fun MatrixCalculatorScreen() {
    var r1      by remember { mutableStateOf("") }
    var r2      by remember { mutableStateOf("") }
    var c1      by remember { mutableStateOf("") }
    var c2      by remember { mutableStateOf("") }
    var mat1    by remember { mutableStateOf("") }
    var mat2    by remember { mutableStateOf("") }

    var resultArray by remember { mutableStateOf<FloatArray?>(null) }
    var error       by remember { mutableStateOf("") }
}