import java.util.concurrent.locks.ReentrantReadWriteLock
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
        accountLock.readLock().withLock {
            return accounts[index].amount
        }
    }

    private enum class Lock {
        READ,
        WRITE,
    }

    private fun lockAccounts(accounts: Array<Account>, lock: Lock) =
        accounts.forEach { account ->
            val accountLock = account.lock
            when (lock) {
                Lock.READ -> accountLock.readLock().lock()
                Lock.WRITE -> accountLock.writeLock().lock()
            }
        }

    private fun unlockAccounts(accounts: Array<Account>, lock: Lock) =
        accounts.forEach { account ->
            val accountLock = account.lock
            when (lock) {
                Lock.READ -> accountLock.readLock().unlock()
                Lock.WRITE -> accountLock.writeLock().unlock()
            }
        }

    override val totalAmount: Long
        get() {
            lockAccounts(accounts, Lock.READ)
            val result = accounts.sumOf { account ->
                account.amount
            }
            unlockAccounts(accounts, Lock.READ)
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
        accountLock.writeLock().withLock {
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
        firstLock.writeLock().withLock {
            secondLock.writeLock().withLock {
                checkUnderflow(amount, from)
                checkOverflow(amount, to)
                from.amount -= amount
                to.amount += amount
            }
        }
    }

    override fun consolidate(fromIndices: List<Int>, toIndex: Int) {
        require(fromIndices.isNotEmpty()) { "empty fromIndices" }
        require(fromIndices.distinct() == fromIndices) { "duplicates in fromIndices" }
        require(toIndex !in fromIndices) { "toIndex in fromIndices" }
        val to = accounts[toIndex]
        val fromToList = (fromIndices + toIndex)
            .sortedBy { it }
            .map { accounts[it] }
            .toTypedArray()
        lockAccounts(fromToList, Lock.WRITE)
        val amount = fromToList.sumOf { account ->
            if (account != to) account.amount else 0
        }
        try {
            checkOverflow(amount, to)
            to.amount += amount
            for (from in fromToList) {
                if (from == to) continue
                from.amount = 0
            }
        } finally {
            unlockAccounts(fromToList, Lock.WRITE)
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
        val lock = ReentrantReadWriteLock()
    }
}
