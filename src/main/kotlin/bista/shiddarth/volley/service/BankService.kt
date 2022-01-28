package bista.shiddarth.volley.service

import bista.shiddarth.volley.dataSource.BankDataSource
import bista.shiddarth.volley.model.Bank
import org.springframework.stereotype.Service

@Service
class BankService(private val dataSource: BankDataSource) {
    fun getBanks(): Collection<Bank> = dataSource.retrieveBanks()

    fun getBank(accountNumber: String) = dataSource.retrieveBank(accountNumber)

    fun addBank(bank: Bank) = dataSource.createBank(bank)
    fun updateBank(bank: Bank) = dataSource.updateBank(bank)
    fun deleteBank(accountNumber: String):Unit    = dataSource.deleteBank(accountNumber)
}