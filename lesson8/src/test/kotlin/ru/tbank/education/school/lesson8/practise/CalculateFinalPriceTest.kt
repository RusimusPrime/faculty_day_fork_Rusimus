package ru.tbank.education.school.lesson8.practise

/**
 *
 * Сценарии для тестирования:
 *
 * 1. Позитивные сценарии (happy path):
 *    - Обычный случай: basePrice = 1000, discount = 10%, tax = 20% → проверить корректность формулы.
 *    - Без скидки: discountPercent = 0 → итог = basePrice + налог.
 *    - Без налога: taxPercent = 0 → итог = basePrice минус скидка.
 *    - Без скидки и без налога: итог = basePrice.
 *
 * 2. Негативные сценарии (исключения):
 *    - Отрицательная цена: basePrice < 0 → IllegalArgumentException.
 *    - Скидка вне диапазона: discountPercent < 0 или > 100 → IllegalArgumentException.
 *    - Налог вне диапазона: taxPercent < 0 или > 30 → IllegalArgumentException.
 */


import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CalculateFinalPriceTest {

    @Test
    fun `valid price should return 1080`() {
        Assertions.assertEquals(1080.0, calculateFinalPrice(basePrice=1000.0,discountPercent=10,taxPercent=20))
    }

    @Test
    fun `valid price without discount should return 1200`() {
        Assertions.assertEquals(1200.0, calculateFinalPrice(basePrice=1000.0,discountPercent=0,taxPercent=20))
    }

    @Test
    fun `valid price without tax should return 900`() {
        Assertions.assertEquals(900.0, calculateFinalPrice(basePrice=1000.0,discountPercent=10,taxPercent=0))
    }

    @Test
    fun `valid price without discount and tax should return 1000`() {
        Assertions.assertEquals(1000.0, calculateFinalPrice(basePrice=1000.0,discountPercent=0,taxPercent=0))
    }



    @Test
    fun `valid negative price should return IllegalArgumentException`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            calculateFinalPrice(basePrice=-1000.0,discountPercent=0,taxPercent=0)
        }
    }

    fun `valid price with too big ot too small discount should return IllegalArgumentException`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            calculateFinalPrice(basePrice=1000.0,discountPercent=-5,taxPercent=0)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            calculateFinalPrice(basePrice=1000.0,discountPercent=120,taxPercent=0)
        }
    }

    fun `valid price with too big ot too small tax should return IllegalArgumentException`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            calculateFinalPrice(basePrice=1000.0,discountPercent=5,taxPercent=-10)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            calculateFinalPrice(basePrice=1000.0,discountPercent=20,taxPercent=50)
        }
    }
}
