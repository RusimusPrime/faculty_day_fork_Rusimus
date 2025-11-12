package ru.tbank.educations.school.lesson1

import kotlin.math.*


enum class OperationType {
    ADD, SUBTRACT, MULTIPLY, DIVIDE,
    SIN, COS, TAN, SQRT, POWER
}


fun calculate(a: Double, b: Double, operation: OperationType = OperationType.ADD): Double? {
    return when (operation) {
        OperationType.ADD -> a + b
        OperationType.SUBTRACT -> a - b
        OperationType.MULTIPLY -> a * b
        OperationType.DIVIDE -> if (b != 0.0) a / b else null
        OperationType.SIN -> sin(a)
        OperationType.COS -> cos(a)
        OperationType.TAN -> tan(a)
        OperationType.SQRT -> if (a >= 0) sqrt(a) else null
        OperationType.POWER -> a.pow(b)
    }
}


fun String.calculate(): Double? {
    val example = this.trim().split(" ")

    return when (example.size) {
        3 -> {
            val a = example[0].toDoubleOrNull() ?: return null
            val b = example[2].toDoubleOrNull() ?: return null

            val operation = when (example[1]) {
                "+" -> OperationType.ADD
                "-" -> OperationType.SUBTRACT
                "*", "x" -> OperationType.MULTIPLY
                "/" -> OperationType.DIVIDE
                "^", "pow" -> OperationType.POWER
                else -> return null
            }

            calculate(a, b, operation)
        }

        2 -> {
            val operation = when (example[0].toLowerCase()) {
                "sin" -> OperationType.SIN
                "cos" -> OperationType.COS
                "tan" -> OperationType.TAN
                "sqrt" -> OperationType.SQRT
                else -> return null
            }

            val a = example[1].toDoubleOrNull() ?: return null
            calculate(a, 0.0, operation)
        }

        else -> null
    }
}


fun calculateWithParentheses(expression: String): Double? {
    var expr = expression.trim()

    while ("(" in expr && ")" in expr) {
        val openIndex = expr.lastIndexOf("(")
        val closeIndex = expr.indexOf(")", startIndex = openIndex)

        if (closeIndex == -1) return null

        val innerExpr = expr.substring(openIndex + 1, closeIndex)
        val innerResult = calculateWithParentheses(innerExpr) ?: innerExpr.calculate()

        if (innerResult == null) return null

        expr = expr.take(openIndex) + innerResult.toString() + expr.substring(closeIndex + 1)
    }

    return expr.calculate()
}

fun calculateBigNumbers(a: String, b: String, operation: OperationType = OperationType.ADD): String? {
    return try {
        val numA = a.toDoubleOrNull() ?: return null
        val numB = b.toDoubleOrNull() ?: return null

        calculate(numA, numB, operation)?.toString()
    } catch (error: Exception) {
        null
    }
}

fun main() {
    println("CIN 45".calculate())
}