package com.example.calculator

import kotlin.math.pow
import kotlin.math.sqrt

class CalculatorEngine {
    private var expression: String = ""

    private fun parseExpression(expression: String): List<String> {
        this.expression = expression

        val outputQueue = mutableListOf<String>()
        val operatorStack = mutableListOf<String>()

        val formattedExpression = expression.replace("×", "*").replace("÷", "/")
        val tokens = formattedExpression.split("(?<=[-+*/^√()=])|(?=[-+*/^√()=])".toRegex())

        var previousTokenWasOperator = true
        var negateNextOperand = false // Flag to track negation before operand

        for (token in tokens) {
            when {
                token.matches("-?\\d+(\\.\\d+)?".toRegex()) -> { // Number (including negative)
                    if (negateNextOperand) {
                        outputQueue.add("-" + token) // Add unary minus directly to the number
                        negateNextOperand = false
                    } else {
                        outputQueue.add(token)
                    }
                    previousTokenWasOperator = false
                }
                token in setOf("+", "-") -> {
                    if (previousTokenWasOperator) {
                        if (token == "-") {
                            negateNextOperand = !negateNextOperand // Toggle negation flag
                        } // Ignore unary plus
                    } else {
                        while (operatorStack.isNotEmpty() &&
                            operatorStack.last() in setOf("+", "-", "*", "/", "^", "√", "u+", "u-")) {
                            outputQueue.add(operatorStack.removeLast())
                        }
                        operatorStack.add(token)
                    }
                    previousTokenWasOperator = true
                }
                token in setOf("*", "/") -> {
                    while (operatorStack.isNotEmpty() &&
                        operatorStack.last() in setOf("*", "/", "^", "√", "u+", "u-")) {
                        outputQueue.add(operatorStack.removeLast())
                    }
                    operatorStack.add(token)
                    previousTokenWasOperator = true
                }
                token in setOf("^", "√") -> {
                    while (operatorStack.isNotEmpty() && operatorStack.last() in setOf("^", "√", "u+", "u-")) {
                        outputQueue.add(operatorStack.removeLast())
                    }
                    operatorStack.add(token)
                    previousTokenWasOperator = true
                }
                token == "(" -> {
                    if (negateNextOperand) {
                        outputQueue.add("u-") // Add unary minus before the opening parenthesis
                        negateNextOperand = false
                    }
                    operatorStack.add(token)
                    previousTokenWasOperator = true
                }
                token == ")" -> {
                    while (operatorStack.isNotEmpty() && operatorStack.last() != "(") {
                        outputQueue.add(operatorStack.removeLast())
                    }
                    if (operatorStack.isNotEmpty() && operatorStack.last() == "(") {
                        operatorStack.removeLast()
                    }
                    previousTokenWasOperator = false
                }
            }
        }

        while (operatorStack.isNotEmpty()) {
            outputQueue.add(operatorStack.removeLast())
        }

        println("Postfix Expression: $outputQueue")
        return outputQueue
    }

    private fun evaluateExpression(postfixExpression: List<String>): String {
        val stack = mutableListOf<Double>()

        for (token in postfixExpression) {
            when {
                token.matches("-?\\d+(\\.\\d+)?".toRegex()) -> { // Number (including negative)
                    stack.add(token.toDouble())
                }
                token == "u-" -> { // Unary minus
                    val operand = stack.removeLastOrNull() ?: return "Format Error"
                    stack.add(-operand) // Negate the operand
                }
                else -> { // Operator
                    val operand2 = stack.removeLastOrNull() ?: return "Format Error"
                    val operand1 = if (token != "√") stack.removeLastOrNull() ?: return "Format Error" else 0.0

                    val result = when (token) {
                        "+" -> operand1 + operand2
                        "-" -> operand1 - operand2
                        "*" -> operand1 * operand2
                        "/" -> if (operand2 != 0.0) operand1 / operand2 else return "Division by zero"
                        "^" -> operand1.pow(operand2)
                        "√" -> sqrt(operand2)
                        else -> throw IllegalArgumentException("Invalid operator: $token")
                    }
                    stack.add(result)
                }
            }
        }

        return stack.lastOrNull()?.toString() ?: "Error"
    }

    fun getResult(expression: String): String {
        var finalResult = evaluateExpression(parseExpression(expression))

        if (finalResult.endsWith(".0")) {
            finalResult = finalResult.substring(0, finalResult.length - 2)
        } else if (finalResult == "null") {
            finalResult = "Error"
        } else if (finalResult == "Infinity") {
            finalResult = "Number too big"
        }

        return finalResult
    }
}