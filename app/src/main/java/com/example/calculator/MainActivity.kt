/* TODO
*   - Negative root ( -√9 ) doesn't work
*   - Fix the landscape mode
*   - Add negative number support
*   - Make light and dark themes
* */

package com.example.calculator

import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.theme.CalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorTheme {
                CalculatorScreen()
            }
        }
    }
}

@Composable
fun CalculatorScreen() {
    val displayText = remember { mutableStateOf("") }
    val resultText = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(36, 36, 36))
    ) {
        Box(modifier = Modifier.weight(1.5f)) {
            DisplayScreen(displayText, resultText)
        }
        Box(modifier = Modifier.weight(2.5f)) {
            ButtonScreen(displayText, resultText)
        }
    }
}

@Composable
fun DisplayScreen(displayText: MutableState<String>, resultText: MutableState<String>) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(48, 48, 48)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.BottomEnd
        ) {
            DisplayText(text = displayText.value)
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.5f),
            contentAlignment = Alignment.BottomEnd
        ) {
            ResultText(text = resultText.value)
        }
    }
}

@Composable
fun ButtonScreen(
    displayText: MutableState<String>,
    resultText: MutableState<String>,
    context: Context = LocalContext.current
) {
    val calculatorEngine = remember { CalculatorEngine() }
    val vibrator = context.getSystemService(Vibrator::class.java)
    var parenthesisCount = 0
    var canEnterDecimalPoint = true
    var isANewTerm = true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(36, 36, 36))
            .padding(vertical = 16.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ClearButton(
                onClick = {
                    parenthesisCount = 0
                    canEnterDecimalPoint = true
                    isANewTerm = true
                    if (displayText.value.isNotEmpty()) {
                        displayText.value = ""
                    } else {
                        displayText.value = ""
                        resultText.value = ""
                    }
                },
                vibrator = vibrator,
                modifier = Modifier.weight(1f)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(Color(36, 36, 36)),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OperatorButton(
                    operator = R.drawable.operator_root,
                    onClick = {
                        if (!displayText.value.endsWith("√") || !displayText.value.endsWith(".") || !displayText.value.last().isDigit()) {
                            displayText.value += "√"
                            canEnterDecimalPoint = false
                            isANewTerm = true
                        }
                    },
                    vibrator = vibrator,
                    modifier = Modifier.weight(1f),
                    contentDescription = "√"
                )
                OperatorButton(
                    operator = R.drawable.operator_open_parenthesis,
                    onClick = { operator ->
                        if (displayText.value.isEmpty() || !(displayText.value.last().isDigit() || displayText.value.endsWith("."))) {
                            displayText.value += operator
                            parenthesisCount += 1
                            canEnterDecimalPoint = true
                            isANewTerm = true
                        }
                    }, vibrator = vibrator,
                    modifier = Modifier.weight(1f),
                    contentDescription = "("
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(Color(36, 36, 36)),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OperatorButton(operator = R.drawable.operator_exponent, onClick = {
                    canEnterDecimalPoint = false
                    isANewTerm = true
                    if (!displayText.value.endsWith("^") && !displayText.value.endsWith("√") && !displayText.value.endsWith(
                            "("
                        )
                        && !displayText.value.endsWith("÷") && !displayText.value.endsWith("×") && !displayText.value.endsWith(
                            "-"
                        )
                        && !displayText.value.endsWith("+") && !displayText.value.endsWith(".") && displayText.value.isNotEmpty()
                    ) {
                        displayText.value += "^"
                    }
                }, vibrator = vibrator,
                    modifier = Modifier.weight(1f),
                    contentDescription = "^",
                    iconScale = 0.8f
                )
                OperatorButton(operator = R.drawable.operator_close_parenthesis, onClick = {
                    canEnterDecimalPoint = false
                    isANewTerm = true
                    if (!displayText.value.endsWith("(") && parenthesisCount > 0 && displayText.value.isNotEmpty()) {
                        displayText.value += ")"
                        parenthesisCount -= 1
                    }
                }, vibrator = vibrator, modifier = Modifier.weight(1f),
                    contentDescription = ")"
                )
            }
            OperatorButton(operator = R.drawable.operator_divide, onClick = {
                canEnterDecimalPoint = false
                isANewTerm = true
                if (!displayText.value.endsWith("^") && !displayText.value.endsWith("√") && !displayText.value.endsWith(
                        "("
                    )
                    && !displayText.value.endsWith("÷") && !displayText.value.endsWith("×") && !displayText.value.endsWith(
                        "-"
                    )
                    && !displayText.value.endsWith("+") && !displayText.value.endsWith(".") && displayText.value.isNotEmpty()
                ) {
                    displayText.value += "÷"
                }
            }, vibrator = vibrator, modifier = Modifier.weight(1f),
                contentDescription = "÷"
            )
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NumberButton(
                R.drawable.number_7,
                onClick = {
                    number -> displayText.value += number
                    if (isANewTerm) {
                        canEnterDecimalPoint = true
                    }
                    isANewTerm = false
                          },
                vibrator = vibrator,
                modifier = Modifier.weight(1f),
                contentDescription = "7"
            )
            NumberButton(
                R.drawable.number_8,
                onClick = {
                    number -> displayText.value += number
                    if (isANewTerm) {
                        canEnterDecimalPoint = true
                    }
                    isANewTerm = false
                          },
                vibrator = vibrator,
                modifier = Modifier.weight(1f),
                contentDescription = "8"
            )
            NumberButton(
                R.drawable.number_9,
                onClick = {
                    number -> displayText.value += number
                    if (isANewTerm) {
                        canEnterDecimalPoint = true
                    }
                    isANewTerm = false
                          },
                vibrator = vibrator,
                modifier = Modifier.weight(1f),
                contentDescription = "9"
            )
            OperatorButton(operator = R.drawable.operator_multiply, onClick = {
                canEnterDecimalPoint = false
                isANewTerm = true
                if (!displayText.value.endsWith("^") && !displayText.value.endsWith("√") && !displayText.value.endsWith(
                        "("
                    )
                    && !displayText.value.endsWith("÷") && !displayText.value.endsWith("×") && !displayText.value.endsWith(
                        "-"
                    )
                    && !displayText.value.endsWith("+") && !displayText.value.endsWith(".") && displayText.value.isNotEmpty()
                ) {
                    displayText.value += "×"
                }
            }, vibrator = vibrator, modifier = Modifier.weight(1f),
                contentDescription = "×",
                iconScale = 0.9f
            )
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NumberButton(
                R.drawable.number_4,
                onClick = {
                    number -> displayText.value += number
                    if (isANewTerm) {
                        canEnterDecimalPoint = true
                    }
                    isANewTerm = false
                          },
                vibrator = vibrator,
                modifier = Modifier.weight(1f),
                contentDescription = "4"
            )
            NumberButton(
                R.drawable.number_5,
                onClick = {
                    number -> displayText.value += number
                    if (isANewTerm) {
                        canEnterDecimalPoint = true
                    }
                    isANewTerm = false
                          },
                vibrator = vibrator,
                modifier = Modifier.weight(1f),
                contentDescription = "5"
            )
            NumberButton(
                R.drawable.number_6,
                onClick = {
                    number -> displayText.value += number
                    if (isANewTerm) {
                        canEnterDecimalPoint = true
                    }
                    isANewTerm = false
                          },
                vibrator = vibrator,
                modifier = Modifier.weight(1f),
                contentDescription = "6"
            )
            OperatorButton(operator = R.drawable.operator_minus, onClick = {
                canEnterDecimalPoint = false
                isANewTerm = true
                if (!displayText.value.endsWith("^") && !displayText.value.endsWith("√") && !displayText.value.endsWith("÷") &&
                    !displayText.value.endsWith("×") && !displayText.value.endsWith("-") && !displayText.value.endsWith("+") &&
                    !displayText.value.endsWith(".")
                ) {
                    displayText.value += "-"
                }

            }, vibrator = vibrator, modifier = Modifier.weight(1f),
                contentDescription = "-",
                iconScale = 0.9f
            )
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NumberButton(
                R.drawable.number_1,
                onClick = {
                    number -> displayText.value += number
                    if (isANewTerm) {
                        canEnterDecimalPoint = true
                    }
                    isANewTerm = false
                          },
                vibrator = vibrator,
                modifier = Modifier.weight(1f),
                contentDescription = "1"
            )
            NumberButton(
                R.drawable.number_2,
                onClick = {
                    number -> displayText.value += number
                    if (isANewTerm) {
                        canEnterDecimalPoint = true
                    }
                    isANewTerm = false
                          },
                vibrator = vibrator,
                modifier = Modifier.weight(1f),
                contentDescription = "2"
            )
            NumberButton(
                R.drawable.number_3,
                onClick = {
                    number -> displayText.value += number
                    if (isANewTerm) {
                        canEnterDecimalPoint = true
                    }
                    isANewTerm = false
                          },
                vibrator = vibrator,
                modifier = Modifier.weight(1f),
                contentDescription = "3"
            )
            OperatorButton(R.drawable.operator_plus, onClick = {
                canEnterDecimalPoint = false
                isANewTerm = true
                if (!displayText.value.endsWith("^") && !displayText.value.endsWith("√") && !displayText.value.endsWith(
                        "("
                    )
                    && !displayText.value.endsWith("÷") && !displayText.value.endsWith("×") && !displayText.value.endsWith(
                        "-"
                    )
                    && !displayText.value.endsWith("+") && !displayText.value.endsWith(".") && displayText.value.isNotEmpty()
                ) {
                    displayText.value += "+"
                }
            }, vibrator = vibrator, modifier = Modifier.weight(1f),
                contentDescription = "+"
            )
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NumberButton(
                R.drawable.number_0,
                onClick = {
                    number -> displayText.value += number
                    canEnterDecimalPoint = true
                    isANewTerm = false
                          },
                vibrator = vibrator,
                modifier = Modifier.weight(1f),
                contentDescription = "0"
            )
            NumberButton(
                R.drawable.decimal_point,
                onClick = {
                    if (canEnterDecimalPoint && displayText.value.isNotEmpty()) {
                        displayText.value += "."
                        canEnterDecimalPoint = false
                        isANewTerm = false
                    }
                },
                vibrator = vibrator,
                modifier = Modifier.weight(1f),
                contentDescription = ".",
                iconScale = 0.25f
            )
            BackspaceButton(
                onClick = {
                    if (displayText.value.isNotEmpty()) {
                        if (displayText.value.endsWith("(")) {
                            parenthesisCount -= 1
                        }
                        if (displayText.value.endsWith(")")) {
                            parenthesisCount += 1
                        }
                        if (displayText.value.endsWith(".")) {
                            canEnterDecimalPoint = true
                        }
                        displayText.value = displayText.value.dropLast(1)
                    } else {
                        parenthesisCount = 0
                        canEnterDecimalPoint = true
                        isANewTerm = true
                    }
                },
                vibrator = vibrator,
                modifier = Modifier.weight(1f),
            )
            EqualButton(
                onClick = {
                    if (displayText.value.isNotEmpty()) {
                        resultText.value = calculatorEngine.getResult(displayText.value)
                    }
                    if (parenthesisCount != 0) {
                        resultText.value = "Check parenthesis"
                    }
                    if (displayText.value.endsWith(".")) {
                        resultText.value = "Check decimal point"
                    }
                },
                vibrator = vibrator,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun DisplayText(text: String) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState, reverseScrolling = true)
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Text(
            text = text,
            fontSize = 72.sp,
            textAlign = TextAlign.End,
            color = Color(240, 240, 240)
        )
    }
}

@Composable
fun ResultText(text: String) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Text(
            text = text,
            fontSize = 72.sp,
            textAlign = TextAlign.End,
            color = Color(200, 200, 200)
        )
    }
}

@Composable
fun NumberButton(
    number: Int,
    contentDescription: String,
    onClick: (String) -> Unit,
    vibrator: Vibrator,
    modifier: Modifier = Modifier,
    iconScale: Float = 1f
) {
    val iconSize by remember { mutableStateOf(38.dp) }

    Button(
        onClick = {
            onClick(contentDescription)
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        },
        modifier = modifier.fillMaxSize(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(48, 48, 48),
            contentColor = Color(240, 240, 240)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            painter = painterResource(id = number),
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize * iconScale)
        )
    }
}

@Composable
fun OperatorButton(
    operator: Int,
    onClick: (String) -> Unit,
    vibrator: Vibrator,
    modifier: Modifier = Modifier,
    contentDescription: String,
    iconScale: Float = 1f
) {
    val iconSize by remember { mutableStateOf(30.dp) }

    Button(
        onClick = {
            onClick(contentDescription)
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        },
        modifier = modifier.fillMaxSize(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(64, 64, 64),
            contentColor = Color(240, 240, 240)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            painter = painterResource(id = operator),
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize * iconScale)
        )
    }
}

@Composable
fun ClearButton(onClick: () -> Unit, vibrator: Vibrator, modifier: Modifier = Modifier) {
    val iconSize by remember { mutableStateOf(42.dp) }

    Button(
        onClick = {
            onClick()
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        },
        modifier = modifier.fillMaxSize(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(90, 0, 0),
            contentColor = Color(240, 240, 240)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(painter = painterResource(id = R.drawable.clear_all),
            contentDescription = "Clear",
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
fun BackspaceButton(onClick: () -> Unit, vibrator: Vibrator, modifier: Modifier = Modifier) {
    Button(
        onClick = {
            onClick()
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        },
        modifier = modifier.fillMaxSize(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(48, 48, 48),
            contentColor = Color(240, 240, 240)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            painter = painterResource(
                id = R.drawable.ic_backspace
            ),
            contentDescription = "Backspace"
        )
    }
}

@Composable
fun EqualButton(onClick: () -> Unit, vibrator: Vibrator, modifier: Modifier = Modifier) {
    val iconSize by remember { mutableStateOf(24.dp) }

    Button(
        onClick = {
            onClick()
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        },
        modifier = modifier.fillMaxSize(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 1f),
            contentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 1f)
        )
    ) {
        Icon(painter = painterResource(id = R.drawable.operator_equal),
            contentDescription = "Equal",
            modifier = Modifier.size(iconSize)
        )
    }
}

@PreviewScreenSizes
@Preview
@Composable
fun CalculatorScreenPreview() {
    CalculatorTheme {
        CalculatorScreen()
    }
}