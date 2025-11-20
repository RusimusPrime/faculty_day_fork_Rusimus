// я хочу добавить еще один тип студента, которому дают скидку
// но не хочу раздувать when
// придумайте как это можно решить
class DiscountCalculator {
    fun calculateDiscount(studentType: String, price: Int): Int {
        return when (studentType) {
            "отличник" -> (price * 0.5).toInt()   // 50% скидка
            "льготник" -> (price * 0.7).toInt()   // платит 70%
            "обычный" -> price                   // без скидки
            else -> price                   // неизвестный тип
        }
    }
}


interface DiscountCalculator {
    fun calculateDiscount(price: Int): Int
}

class ExcellentStudent : DiscountCalculator {
    override fun calculateDiscount(price: Int): Int {
        return (price * 0.5).toInt()
    }
}

class BeneficiaryStudent : DiscountCalculator {
    override fun calculateDiscount(price: Int): Int {
        return (price * 0.7).toInt()
    }
}

class OrdinaryStudent : DiscountCalculator {
    override fun calculateDiscount(price: Int): Int {
        return price
    }
}

class StudentWithAchievements : DiscountCalculator {
    override fun calculateDiscount(price: Int): Int {
        return 0
    }
}

fun main() {
    val a = ExcellentStudent()
    println(a.calculateDiscount(100))
}