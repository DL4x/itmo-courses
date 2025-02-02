class BankAccount(amount: Int) {
    init {
        checkBalance(amount)
    }

    var balance: Int = amount
        private set(nextBalance) {
            checkBalance(nextBalance)
            logTransaction(field, nextBalance)
            field = nextBalance
        }

    private fun checkBalance(amount: Int) =
        require(amount >= 0) {
            "Account can't has negative balance"
        }

    fun deposit(amount: Int) {
        require(amount > 0) {
            "Impossible to deposit a negative or zero amount"
        }
        balance += amount
    }

    fun withdraw(amount: Int) {
        require(amount > 0) {
            "Impossible to withdraw a negative or zero amount"
        }
        balance -= amount
    }
}

fun logTransaction(from: Int, to: Int) {
    println("$from -> $to")
}
