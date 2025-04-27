package com.example.matop


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.matop.ui.theme.MatOpTheme

class MainActivity : ComponentActivity() {

    external fun addMatrices(matrixA: FloatArray, matrixB: FloatArray): FloatArray
    external fun subtractMatrices(matrixA: FloatArray, matrixB: FloatArray): FloatArray
    external fun multiplyMatrices(matrixA: FloatArray, matrixB: FloatArray, n: Int, m: Int, p: Int): FloatArray
    external fun divideMatrices(matrixA: FloatArray, matrixB: FloatArray): FloatArray
    external fun isMatrixInvertible(matrixA: FloatArray,n: Int)
    external fun invertMatrix(matrixA: FloatArray,n: Int): FloatArray

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

    val context  = LocalContext.current
    val activity = context as? MainActivity

    var selectedOperation by remember { mutableStateOf("Add") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment   = Alignment.CenterHorizontally,
            verticalArrangement   = Arrangement.spacedBy(12.dp),
            modifier              = Modifier
                .height(600.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            TextField(
                value       = mat1,
                onValueChange = { mat1 = it },
                label       = { Text("Matrix 1 (comma-separated)") },
                modifier    = Modifier.fillMaxWidth()
            )
            TextField(
                value       = mat2,
                onValueChange = { mat2 = it },
                label       = { Text("Matrix 2 (comma-separated)") },
                modifier    = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value       = r1,
                    onValueChange = { r1 = it },
                    label       = { Text("Rows 1") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier    = Modifier.weight(1f)
                )
                TextField(
                    value       = c1,
                    onValueChange = { c1 = it },
                    label       = { Text("Cols 1") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier    = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value       = r2,
                    onValueChange = { r2 = it },
                    label       = { Text("Rows 2") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier    = Modifier.weight(1f)
                )
                TextField(
                    value       = c2,
                    onValueChange = { c2 = it },
                    label       = { Text("Cols 2") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier    = Modifier.weight(1f)
                )
            }

            Text("Select Operation:")
            Column {
                listOf("Add", "Subtract", "Multiply", "Divide","Mat Divide").forEach { op ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedOperation == op,
                            onClick  = { selectedOperation = op }
                        )
                        Text(text = op)
                    }
                }
            }

            // --- Calculate button ---
            Button(onClick = {
                error = ""
                resultArray = null

                val nr1 = r1.toIntOrNull() ?: 0
                val nc1 = c1.toIntOrNull() ?: 0
                val nr2 = r2.toIntOrNull() ?: 0
                val nc2 = c2.toIntOrNull() ?: 0

                if (nr1 <= 0 || nc1 <= 0) {
                    error = "Matrix 1 dimensions invalid"
                    return@Button
                }
                if (nr2 <= 0 || nc2 <= 0) {
                    error = "Matrix 2 dimensions invalid"
                    return@Button
                }

                val size1 = nr1 * nc1
                val size2 = nr2 * nc2
                val m1 = parseMatrix(mat1, size1)
                val m2 = parseMatrix(mat2, size2)


                when (selectedOperation) {
                    "Add", "Subtract", "Divide" -> {
                        if (nr1 != nr2 || nc1 != nc2) {
                            error = "For $selectedOperation, both matrices must have same total elements"
                            return@Button
                        }
                    }
                    "Multiply","Mat Divide" -> {
                        if (nc1 != nr2) {
                            error = "For Multiply, cols 1 ($nc1) must equal rows 2 ($nr2)"
                            return@Button
                        }
                    }
                }

                try {
                    val res = when (selectedOperation) {
                        "Add"      -> activity?.addMatrices(m1, m2)
                        "Subtract" -> activity?.subtractMatrices(m1, m2)
                        "Multiply" -> {
                            val n = r1.toInt(); val m = c1.toInt(); val p = c2.toInt()
                            activity?.multiplyMatrices(m1, m2, n, m, p)
                        }
                        "Divide"   -> activity?.divideMatrices(m1, m2)
                        "Mat Divide" -> {
                            activity?.isMatrixInvertible(m2,nr1)
                            val invMat = activity?.invertMatrix(m2,nr1)
                            if (invMat == null) {
                                error = "Matrix inverse failed"
                                return@Button
                            }else {
                                activity.multiplyMatrices(m1, invMat, nr1, nc1, nc1)
                            }
                        }
                        else       -> null
                    }
                    resultArray = res
                } catch (e: Exception) {
                    error = "Invalid input: ${e.message}"
                }
            }) {
                Text("Calculate")
            }

            if (error.isNotEmpty()) {
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }

            if (error.isEmpty() && resultArray != null) {
                MatrixResultCard(
                    rows        = r1.toIntOrNull() ?: 0,
                    cols        = c2.toIntOrNull() ?: 0,
                    resultArray = resultArray!!
                )
            }
        }
    }
}

@Composable
fun MatrixResultCard(rows: Int, cols: Int, resultArray: FloatArray) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .height(600.dp)
                .verticalScroll(rememberScrollState()) // Vertical scroll for many rows
        ) {
            Text(text = "Result Matrix:", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()) // <-- Horizontal scroll
            ) {
                Column {
                    for (i in 0 until rows) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (j in 0 until cols) {
                                val index = i * cols + j
                                if (index < resultArray.size) {
                                    Text(
                                        text = "%.2f".format(resultArray[index]),
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .width(60.dp), // fixed width for neatness
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

private fun parseMatrix(input: String, targetSize: Int): FloatArray {
    val tokens = input.split(",")
    return FloatArray(targetSize) { idx ->
        tokens.getOrNull(idx)?.trim()?.toFloatOrNull() ?: 0f
    }
}