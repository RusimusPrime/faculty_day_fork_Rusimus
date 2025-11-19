package ru.tbank.education.school.lesson2.bank

open class Account(
    val id: String,
    val ownerID: String,
    var balance: Double
){
    fun deposit(amount: Double) {
        balance += amount
    }

    open fun withdraw(amount: Double) : Boolean{
        if (balance >= amount){
            balance -= amount
            return true
        }
        return false
    }
}

class LoanAccount(
    id: String,
    balance: Double,
    ownerId: String,
    var creditLimit: Double,
) : Account(
    id = id,
    balance = balance,
    ownerID = ownerId
){
    override fun withdraw(amount: Double): Boolean {
        if (creditLimit + balance >= amount){
            balance -= amount
            return true
        }
        return false
    }
}