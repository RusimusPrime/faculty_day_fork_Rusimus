package ru.tbank.education.school.lesson1

/**
 * Метод для вычисления простых арифметических операций.
 */
fun calculate(a: Double, b: Double, operation: OperationType): Double? {
    return when (operation) {
        OperationType.ADD -> a + b
        OperationType.SUBTRACT -> a - b
        OperationType.MULTIPLY -> a * b
        OperationType.DIVIDE -> if (b != 0.0) a / b else null
    }
}

/**
 * Функция вычисления выражения, представленного строкой
 * @return результат вычисления строки или null, если вычисление невозможно
 * @sample "5 * 2".calculate()
 */
@Suppress("ReturnCount")
fun String.calculate(): Double? {
    val sss = this.split(" ")
    val op = when (sss[1]) {
        "+" -> OperationType.ADD
        "-" -> OperationType.SUBTRACT
        "*" -> OperationType.MULTIPLY
        "/" -> OperationType.DIVIDE
        else -> return null
    }
    return when (sss.size) {
        3 -> when (sss[0].toDoubleOrNull() != null) {
            true -> when (sss[2].toDoubleOrNull() != null) {
                true -> calculate(sss[0].toDouble(), sss[2].toDouble(), op)
                false -> null
            }

            false -> null
        }

        else -> null
    }
}
