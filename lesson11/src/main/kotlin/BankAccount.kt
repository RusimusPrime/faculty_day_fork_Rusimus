package homework


class BankAccount(val id: String, var balance: Int) {
    fun transfer(to: BankAccount, amount: Int) {
        val (first, second) = if (this.id < to.id) this to to else to to this
        synchronized(first) {
            Thread.sleep(10)
            synchronized(second) {
                if (balance >= amount) {
                    balance -= amount
                    to.balance += amount
                }
            }
        }
    }
}
