// 1. Абстрактный класс с общими характеристиками
abstract class Character(
    open val name: String,
    open var health: Int,
    open val maxHealth: Int
) {
    abstract fun firstAction(target: Character? = null)
    abstract fun secondAction(target: Character? = null)
    abstract fun thirdAction(target: Character? = null)

    // Кастомный геттер
    val isAlive: Boolean
        get() = health > 0

    // Protected свойство
    protected var isDefending: Boolean = false

    fun takeDamage(damage: Int) {
        val actualDamage = if (isDefending) damage / 2 else damage
        health -= actualDamage
        if (health < 0) health = 0
        println("$name получает $actualDamage урона! Здоровье: $health/$maxHealth")
    }

    fun heal(amount: Int) {
        health += amount
        if (health > maxHealth) health = maxHealth
        println("$name восстанавливает $amount здоровья! Здоровье: $health/$maxHealth")
    }
}

// 2. Sealed class для состояний игры
sealed class GameState {
    object PlayerTurn : GameState()
    object EnemyTurn : GameState()
    object Victory : GameState()
    object Defeat : GameState()
}

// 3. Data class для хранения данных
data class BattleResult(
    val winner: String,
    val turns: Int,
    val remainingHealth: Int,
    val playersAlive: Int
)

// 4. Базовый класс для игровых персонажей (абстрактный)
abstract class PlayerCharacter(
    name: String,
    health: Int,
    maxHealth: Int,
    val role: String
) : Character(name, health, maxHealth) {

    private var buffs: MutableList<String> = mutableListOf()

    // Кастомный сеттер
    var mana: Int = 50
        set(value) {
            field = when {
                value < 0 -> 0
                value > 100 -> 100
                else -> value
            }
        }

    fun addBuff(buff: String) {
        buffs.add(buff)
        println("$name получает бафф: $buff")
    }

    fun hasBuff(buff: String): Boolean = buffs.contains(buff)

    // общий метод для всех игроков
    open fun showStatus() {
        println("$name ($role) - Здоровье: $health/$maxHealth, Энергия искры: $mana")
    }

    fun getActionDescription(actionNumber: Int): String {
        return when (actionNumber) {
            1 -> when (this) {
                is Adrian -> "Исцелить союзника (25 HP)"
                is Zahir -> "Атака копьем (25 урона)"
                is Milo -> "Теневая стрела (20 урона)"
                else -> "Основная атака"
            }

            2 -> when (this) {
                is Adrian -> "Восстановить энергию искры (+20)"
                is Zahir -> "Защитная стойка (уменьшает урон)"
                is Milo -> "Усилить союзника (+15 энергии искры)"
                else -> "Вторичное действие"
            }

            3 -> when (this) {
                is Adrian -> "Мощное исцеление (40 HP, 30 энергии искры)"
                is Zahir -> "Сокрушительный удар (50 урона, 40 энергии искры)"
                is Milo -> "Теневая гробница (60 урона, 50 энергии искры)"
                else -> "Супер способность"
            }

            else -> "Неизвестное действие"
        }
    }

    // Метод для проверки достаточности энергии
    protected fun hasEnoughMana(requiredMana: Int): Boolean {
        return mana >= requiredMana
    }

    // Метод для использования энергии с проверкой
    protected fun useMana(amount: Int): Boolean {
        if (mana >= amount) {
            mana -= amount
            return true
        }
        return false
    }
}

// 5. Классы-наследники
class Adrian(
    name: String = "Адриан",
    health: Int = 80,
    maxHealth: Int = 80
) : PlayerCharacter(name, health, maxHealth, "Врач-ученый") {

    override fun firstAction(target: Character?) {
        if (target == null) {
            println("$name: Нет цели для исцеления!")
            return
        }
        println("$name исцеляет ${target.name}!")
        target.heal(25)
    }

    override fun secondAction(target: Character?) {
        println("$name концентрируется и восстанавливает энергию искры!")
        mana += 20
        println("Мана: $mana")
    }

    override fun thirdAction(target: Character?) {
        if (target == null) {
            println("$name: Нет цели для исцеления!")
            return
        }
        // Проверяем ману перед использованием способности
        if (!hasEnoughMana(30)) {
            println("Недостаточно энергии! Нужно 30, сейчас: $mana")
            return
        }

        println("$name использует мощное исцеление на ${target.name}!")
        target.heal(40)
        useMana(30) // Используем ману
    }

    override fun showStatus() {
        println("$name ($role) - Здоровье: $health/$maxHealth, Энергия искры: $mana (ЛЕКАРЬ)")
    }
}

class Zahir(
    name: String = "Захир",
    health: Int = 120,
    maxHealth: Int = 120
) : PlayerCharacter(name, health, maxHealth, "Копейщик") {

    override fun firstAction(target: Character?) {
        if (target == null) {
            println("$name: Нет цели для атаки!")
            return
        }
        val damage = if (hasBuff("Ярость")) 35 else 25
        println("$name наносит удар копьем по ${target.name}!")
        target.takeDamage(damage)
    }

    override fun secondAction(target: Character?) {
        println("$name становится в защитную стойку!")
        isDefending = true
        addBuff("Защита")
    }

    override fun thirdAction(target: Character?) {
        if (target == null) {
            println("$name: Нет цели для атаки!")
            return
        }
        // Проверяем ману перед использованием способности
        if (!hasEnoughMana(40)) {
            println("Недостаточно энергии! Нужно 40, сейчас: $mana")
            return
        }

        println("$name использует сокрушительный удар по ${target.name}!")
        target.takeDamage(50)
        useMana(40)
        addBuff("Ярость")
    }
}

class Milo(
    name: String = "Мило",
    health: Int = 90,
    maxHealth: Int = 90,
    role: String = "Агент"
) : PlayerCharacter(name, health, maxHealth, role) {

    // Дополнительный конструктор без role
    constructor(name: String) : this(name, 90, 90, "Агент")

    override fun firstAction(target: Character?) {
        if (target == null) {
            println("$name: Нет цели для атаки!")
            return
        }
        println("$name использует теневую стрелу по ${target.name}!")
        target.takeDamage(20)
        mana += 10
    }

    override fun secondAction(target: Character?) {
        if (target is PlayerCharacter) {
            println("$name усиливает ${target.name}!")
            target.addBuff("Усиление")
            target.mana += 15
        } else {
            println("$name: Не могу усилить эту цель!")
        }
    }

    override fun thirdAction(target: Character?) {
        if (target == null) {
            println("$name: Нет цели для атаки!")
            return
        }
        // Проверяем энергию перед использованием способности
        if (!hasEnoughMana(50)) {
            println("Недостаточно энергии! Нужно 50, сейчас: $mana")
            return
        }

        println("$name вызывает теневую гробницу на ${target.name}!")
        target.takeDamage(60)
        useMana(50)
    }
}

// 6. Класс босса
class CoolTeam(
    name: String = "CoolT3am",
    health: Int = 300,
    maxHealth: Int = 300
) : Character(name, health, maxHealth) {

    private var specialAttackCooldown: Int = 0

    override fun firstAction(target: Character?) {
        // Атакуем всех живых игроков
        Game.currentParty.getAliveMembers().forEach { player ->
            player.takeDamage(15)
        }
    }

    override fun secondAction(target: Character?) {
        if (target != null) {
            val damage = 30
            target.takeDamage(damage)
        }
    }

    override fun thirdAction(target: Character?) {
        if (specialAttackCooldown == 0) {
            Game.isChaosActive = true
            specialAttackCooldown = 3
        } else {
            firstAction(target)
        }
    }

    fun updateCooldown() {
        if (specialAttackCooldown > 0) specialAttackCooldown--
    }

    fun showBossStatus() {
        val cooldownText = if (specialAttackCooldown > 0) " (Перезарядка спец.атаки: $specialAttackCooldown)" else ""
        println("БОСС $name - Здоровье: $health/$maxHealth$cooldownText")
    }

    // Добавляем публичный метод для проверки перезарядки
    fun isSpecialAttackReady(): Boolean = specialAttackCooldown == 0
}

// 7. Класс для управления командой (ассоциация, агрегация)
class Party {
    // Ассоциация: Party содержит ссылки на Character
    private val members: MutableList<PlayerCharacter> = mutableListOf()

    // Агрегация: Party содержит Character, но они могут существовать отдельно
    fun addMember(member: PlayerCharacter) {
        members.add(member)
        println("${member.name} присоединился к группе!")
    }

    fun getAliveMembers(): List<PlayerCharacter> = members.filter { it.isAlive }

    fun isPartyAlive(): Boolean = getAliveMembers().isNotEmpty()

    fun getAllMembers(): List<PlayerCharacter> = members.toList()

    // Метод для очистки партии
    fun clearParty() {
        members.clear()
    }

    fun showPartyStatus() {
        println("\n=== Статус команды ===")
        members.forEachIndexed { index, member ->
            val status = if (member.isAlive) "✅ ЖИВ" else "💀 МЕРТВ"
            println("${index + 1}. ${member.name} (${member.role}) - Здоровье: ${member.health}/${member.maxHealth}, Энергия искры: ${member.mana} - $status")
        }
    }
}

// 8. Класс для управления игрой
object Game {
    private val party = Party()
    private lateinit var boss: CoolTeam
    private var gameState: GameState = GameState.PlayerTurn
    private var turnCount: Int = 0
    var isChaosActive: Boolean = false
    val currentParty: Party get() = party

    fun startGame() {
        initializeGame()

        while (gameState == GameState.PlayerTurn || gameState == GameState.EnemyTurn) {
            turnCount++
            println("\n╔══════════════════════════════╗")
            println("║            ХОД $turnCount            ║")
            println("╚══════════════════════════════╝")

            when (gameState) {
                GameState.PlayerTurn -> playerTurn()
                GameState.EnemyTurn -> enemyTurn()
                else -> break
            }

            checkGameState()
        }

        endGame()
    }

    private fun initializeGame() {
        println("🎮 Добро пожаловать в Death Theatre!")
        println("Ваша задача - победить босса 'CoolT3am'!")

        // Создаем персонажей с разными конструкторами
        party.addMember(Adrian()) // основной конструктор
        party.addMember(Zahir()) // основной конструктор
        party.addMember(Milo("Мило-шпион")) // дополнительный конструктор

        boss = CoolTeam()

        println("\n💀 Появляется БОСС ${boss.name}!")
        println("⚔️  Начинается битва!")
        gameState = GameState.PlayerTurn
    }

    private fun playerTurn() {
        println("\n--- ХОД ИГРОКОВ ---")
        party.showPartyStatus()
        boss.showBossStatus()

        if (isChaosActive) {
            println("\n🌀 ДЕЙСТВУЕТ ХАОС! Цели могут быть перепутаны!")
        }

        val aliveMembers = party.getAliveMembers()
        aliveMembers.forEach { player ->
            if (boss.isAlive && player.isAlive) {
                println("\nХод ${player.name} (${player.role}):")
                println("1 - ${player.getActionDescription(1)}")
                println("2 - ${player.getActionDescription(2)}")
                println("3 - ${player.getActionDescription(3)}")

                var action: Int
                var target: Character? = null

                // Выбор действия
                while (true) {
                    print("Выберите действие (1-3): ")
                    try {
                        action = readlnOrNull()?.toInt() ?: 0
                        if (action in 1..3) break
                        else println("❌ Пожалуйста, введите число от 1 до 3")
                    } catch (e: NumberFormatException) {
                        println("❌ Пожалуйста, введите корректное число")
                    }
                }

                // Выбор цели
                when (action) {
                    1 -> target = chooseTarget(player, true)
                    2 -> target = chooseTarget(player, false)
                    3 -> target = chooseTarget(player, true)
                }

                // Применение хаоса (случайная цель)
                if (isChaosActive && (1..3).random() == 1) {
                    val possibleTargets = party.getAllMembers() + boss
                    target = possibleTargets.random()
                    println("🌀 ХАОС! ${player.name} атакует случайную цель: ${target.name}!")
                }

                // Выполнение действия
                when (action) {
                    1 -> player.firstAction(target)
                    2 -> player.secondAction(target)
                    3 -> player.thirdAction(target)
                }

                // Проверка состояния босса после каждого действия
                if (!boss.isAlive) {
                    println("\n🎉 ${player.name} наносит последний удар!")
                    return
                }
            }
        }

        isChaosActive = false
        gameState = GameState.EnemyTurn
    }

    private fun chooseTarget(currentPlayer: PlayerCharacter, canTargetEnemy: Boolean): Character? {
        val aliveMembers = party.getAliveMembers()

        if (canTargetEnemy) {
            println("\n🎯 Выберите цель:")
            var optionNumber = 1
            val targetOptions = mutableListOf<Character>()

            // Добавляем босса как первую опцию
            println("$optionNumber - Босс ${boss.name} (Здоровье: ${boss.health}/${boss.maxHealth})")
            targetOptions.add(boss)
            optionNumber++

            // Добавляем союзников (кроме текущего игрока)
            aliveMembers.forEach { member ->
                if (member != currentPlayer) {
                    println("$optionNumber - ${member.name} (Здоровье: ${member.health}/${member.maxHealth})")
                    targetOptions.add(member)
                    optionNumber++
                }
            }

            while (true) {
                print("Выберите цель (1-${targetOptions.size}): ")
                try {
                    val choice = readlnOrNull()?.toInt() ?: 0
                    if (choice in 1..targetOptions.size) {
                        return targetOptions[choice - 1]
                    } else {
                        println("❌ Пожалуйста, введите число от 1 до ${targetOptions.size}")
                    }
                } catch (e: NumberFormatException) {
                    println("❌ Пожалуйста, введите корректное число")
                }
            }
        } else {
            // Для поддержки - только союзники
            val availableAllies = aliveMembers.filter { it != currentPlayer }
            if (availableAllies.isEmpty()) {
                println("❌ Нет доступных союзников для усиления!")
                return null
            }

            println("\nВыберите союзника для усиления:")
            availableAllies.forEachIndexed { index, member ->
                println("${index + 1} - ${member.name} (Здоровье: ${member.health}/${member.maxHealth}, Энергия искры ${member.mana})")
            }

            while (true) {
                print("Выберите союзника (1-${availableAllies.size}): ")
                try {
                    val choice = readlnOrNull()?.toInt() ?: 0
                    if (choice in 1..availableAllies.size) {
                        return availableAllies[choice - 1]
                    } else {
                        println("❌ Пожалуйста, введите число от 1 до ${availableAllies.size}")
                    }
                } catch (e: NumberFormatException) {
                    println("❌ Пожалуйста, введите корректное число")
                }
            }
        }
    }

    private fun enemyTurn() {
        println("\n--- ХОД БОССА ---")

        if (boss.isAlive) {
            val action = (1..3).random()
            val target = party.getAliveMembers().randomOrNull()

            when (action) {
                1 -> {
                    println("${boss.name} атакует всех врагов силой хаоса!")
                    boss.firstAction(target)
                }

                2 -> {
                    if (target != null) {
                        println("${boss.name} направляет мощную атаку на ${target.name}!")
                        boss.secondAction(target)
                    } else {
                        println("${boss.name}: Нет цели для атаки!")
                    }
                }

                3 -> {
                    if (boss.isSpecialAttackReady()) {
                        println("${boss.name} использует Хаос! Герои могут атаковать не тех целей!")
                        boss.thirdAction(target)
                    } else {
                        println("${boss.name} атакует всех врагов силой хаоса!")
                        boss.firstAction(target)
                    }
                }
            }

            boss.updateCooldown()
        }

        gameState = GameState.PlayerTurn
    }

    private fun checkGameState() {
        if (!boss.isAlive) {
            gameState = GameState.Victory
        } else if (!party.isPartyAlive()) {
            gameState = GameState.Defeat
        }
    }

    private fun endGame() {
        val result = BattleResult(
            winner = if (gameState == GameState.Victory) "Игроки" else "Босс",
            turns = turnCount,
            remainingHealth = party.getAliveMembers().sumOf { it.health },
            playersAlive = party.getAliveMembers().size
        )

        println("\n⭐" + "=".repeat(40) + "⭐")
        println("           ИГРА ОКОНЧЕНА!")
        println("⭐" + "=".repeat(40) + "⭐")

        println("\n📊 Результаты битвы:")
        println("🏆 Победитель: ${result.winner}")
        println("🔄 Количество ходов: ${result.turns}")
        println("❤️  Выживших героев: ${result.playersAlive}")
        println("💚 Общее здоровье команды: ${result.remainingHealth}")

        when (gameState) {
            GameState.Victory -> println("\nПОЗДРАВЛЯЕМ! Вы победили ${boss.name}!")
            GameState.Defeat -> println("\nВЫ ПРОИГРАЛИ... ${boss.name} оказался слишком силен!")
            else -> println("\n❓ Неожиданный результат игры!")
        }

        // Предложение сыграть еще раз
        println("\nХотите сыграть еще раз? (да/нет)")
        val answer = readlnOrNull()?.lowercase()
        if (answer == "да" || answer == "yes" || answer == "y" || answer == "д") {
            resetGame()
            startGame()
        } else {
            println("👋 Спасибо за игру!")
        }
    }

    private fun resetGame() {
        // Создаем новую партию вместо очистки
        party.clearParty()
        gameState = GameState.PlayerTurn
        turnCount = 0
        isChaosActive = false
    }
}

// 9. Демонстрация работы системы
fun main() {
    println("ТЕКСТОВАЯ RPG - Death theatre")
    println()
    println("Правила игры:")
    println("• Управляйте тремя героями против босса")
    println("• Каждый герой имеет уникальные способности")
    println("• Босс может использовать специальные атаки")
    println("• Победите босса, чтобы выиграть!")
    println()
    println("Управление:")
    println("• Выбирайте действий цифрами 1, 2, 3")
    println("• Выбирайте цели из предложенного списка")
    println("• Следите за энергией искры и здоровьем героев")
    println()

    Game.startGame()
}


