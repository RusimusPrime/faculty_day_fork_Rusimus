package ru.tbank.education.school.lesson1

fun main() {
//    print("Hello World!")
//    println("Hello World!")
//    println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAaaa")
//
//    val a = 1
//    val b = 's' // char
//    val c = "s" // string
//    val d = 1.23
//    val e = """Организация проекта
//Структура проекта
//Каждый модуль проекта - отдельный урок согласно плану занятий.
//
//Описание занятия и его задания находятся в соответствующей директории.
//
//Сборка проекта
//Проект организован как multi project build с использованием buildSrc
//
//Подробнее в документации Gradle
//
//Пример официальной документации
//
//Пример инициализации проекта через gradle init
//
//Соглашения по проекту
//Проверка форматирования - detekt
//Написание юнит тестов - junit5
//Мокирование зависимостей и проверка вызовов - mockk
//Проверка структуры проекта - archunit
//Проверка структуры классов и наименований - konsist""".trimIndent()
//    val f = true
//    val g = "hello $d world"
//    println("$a $b $c $d $e")
//    val h: Byte = 1
//    // val не изменятеся, var изменяется

    val str = "Ruslan"
    println("My name is $str")

    var number = 5
    println("before: $number")
    number++
    println("after: $number")

    val unknown = null
    println("unknown: $unknown")

    val array = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val doubleDimensionArray = Array(2){
        Array(2){0}
    }
    val l = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    for (i in array.indices) {
        println(array[i])
    }

    for (i in 1..10){
        print("$i ")
    }

    for (i in 6 downTo 0 step 2){
        print("$i ")
    }

    throw IllegalArgumentException("")
}
