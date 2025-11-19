package ru.tbank.education.school.lesson2.bank

class Bank {
    var accountSeq = 0
    var clientSeq = 0

    private val clients: MutableList<Person> = mutableListOf()
    private val accounts: MutableList<Account> = mutableListOf()

    fun addCustomer(name: String) {
        clients.add(
            Person(
                name = name,
                id = "C-${clientSeq++}"
            )
        )
    }


    fun addAccount(clientId: String) {
        accounts.add(
            Account(
                id = "A-${accountSeq++}",
                balance = 0.0,
                ownerID = clientId
            )
        )
    }

    fun transfer(fromAccountId: String, toAccountId: String, amount: Double) {
        val fromAccount = accounts.find {it.id == fromAccountId}!!
        val toAccount = accounts.find {it.id == toAccountId}!!

        if (fromAccount.withdraw(amount)){
            toAccount.deposit(amount)
        }
    }
}