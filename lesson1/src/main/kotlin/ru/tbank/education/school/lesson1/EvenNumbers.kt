package ru.tbank.education.school.lesson1

/**
 * Сумма четных чисел.
 */
fun sumEvenNumbers(numbers: Array<Int>): Int {
    var sum = 0
    for (number in numbers) {
        sum += if (number % 2 == 0) number else 0
    }
    return sum
}
