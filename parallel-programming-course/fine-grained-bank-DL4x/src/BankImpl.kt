import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Bank implementation.
 *
 * @author Shulpin Egor
 */
class BankImpl(n: Int) : Bank {
    private val accounts: Array<Account> = Array(n) { Account() }

    override val numberOfAccounts: Int
        get() = accounts.size

    override fun getAmount(index: Int): Long {
        val account = accounts[index]
        val accountLock = account.lock
        accountLock.withLock {
            return account.amount
        }
    }

    override val totalAmount: Long
        get() {
            accounts.forEach { account ->
                val accountLock = account.lock
                accountLock.lock()
            }
            val result = accounts.sumOf { account ->
                account.amount
            }
            accounts.forEach { account ->
                val accountLock = account.lock
                accountLock.unlock()
            }
            return result
        }

    private fun checkUnderflow(amount: Long, account: Account) =
        check(amount <= account.amount) { "Underflow" }

    private fun checkOverflow(amount: Long, account: Account) =
        check(!(amount > Bank.MAX_AMOUNT || account.amount + amount > Bank.MAX_AMOUNT)) { "Overflow" }

    private fun accountAmountOperation(
        index: Int,
        amount: Long,
        checkException: (Account) -> Unit,
        amountOperation: (Account) -> Unit,
    ): Long {
        require(amount > 0) { "Invalid amount: $amount" }
        val account = accounts[index]
        val accountLock = account.lock
        accountLock.withLock {
            checkException(account)
            amountOperation(account)
            return account.amount
        }
    }

    override fun deposit(index: Int, amount: Long) =
        accountAmountOperation(
            index = index,
            amount = amount,
            checkException = { account -> checkOverflow(amount, account) },
            amountOperation = { account -> account.amount += amount },
        )

    override fun withdraw(index: Int, amount: Long) =
        accountAmountOperation(
            index = index,
            amount = amount,
            checkException = { account -> checkUnderflow(amount, account) },
            amountOperation = { account -> account.amount -= amount },
        )

    override fun transfer(fromIndex: Int, toIndex: Int, amount: Long) {
        require(amount > 0) { "Invalid amount: $amount" }
        require(fromIndex != toIndex) { "fromIndex == toIndex" }
        val from = accounts[fromIndex]
        val to = accounts[toIndex]
        val (firstLock, secondLock) =
            if (fromIndex < toIndex) {
                from.lock to to.lock
            } else {
                to.lock to from.lock
            }
        firstLock.withLock {
            secondLock.withLock {
                checkUnderflow(amount, from)
                checkOverflow(amount, to)
                from.amount -= amount
                to.amount += amount
            }
        }
    }

    /**
     * Private account data structure.
     */
    class Account {
        /**
         * Amount of funds in this account.
         */
        var amount: Long = 0
        val lock = ReentrantLock()
    }
}
