package com.dh.myapplication.core.network

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Numeric
import java.math.BigInteger

class Blockchain {


    companion object {
        private const val TAG = "blockchain"
    }

        // Web3j instance for interacting with Ethereum blockchain
        private val web3j: Web3j = Web3j.build(HttpService("https://eth-sepolia.g.alchemy.com/v2/7xAqSS8-Q_v3EAoqkaMVV5Fuq9XJUeOu"))
        // Function to get the balance of an Ethereum address
        fun getBalance(address: String): BigInteger {
            // Retrieve the balance using web3j library
            val balanceResult = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send()
            return balanceResult.balance
        }
    // Function to transfer Ethereum from one address to another
   /* @RequiresApi(Build.VERSION_CODES.N)
    fun transfer(
        fromAddress: String,
        toAddress: String,
        privateKey: String,
        message: String
    ): TransactionReceipt {
        // Create credentials from private key
        val credentials = Credentials.create(privateKey)
        // Set gas price, gas limit, and nonce
        val gasPrice = BigInteger.valueOf(20000000000L)
        val gasLimit = BigInteger.valueOf(60000)
        val nonce = web3j.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.LATEST)
            .send().transactionCount
        // Create a raw transaction
        val rawTransaction = RawTransaction.createTransaction(
            nonce,
            gasPrice,
            gasLimit,
            toAddress,
            BigInteger.ZERO,
            message
        )
        // Sign and send the transaction
        val signedTransaction = TransactionEncoder.signMessage(rawTransaction, credentials)
        val hexValue = Numeric.toHexString(signedTransaction)
        val transactionHash = web3j.ethSendRawTransaction(hexValue).send().transactionHash
        val receipt = web3j.ethGetTransactionReceipt(transactionHash).send().transactionReceipt
        return receipt.orElse(null)
    }
*/
    // Suspended function to transfer Ethereum with a callback for receipt
    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun transfer2(
        fromAddress: String,
        toAddress: String,
        privateKey: String,
        message: String,
        callback: (TransactionReceipt?) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            // Create credentials from private key
            val credentials = Credentials.create(privateKey)
            // Set gas price, gas limit, and nonce
            val gasPrice = BigInteger.valueOf(200000000000L)
            val gasLimit = BigInteger.valueOf(60000)
            val nonce = web3j.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.LATEST)
                .send().transactionCount
            // Create a raw transaction
            val rawTransaction = RawTransaction.createTransaction(
                nonce,
                gasPrice,
                gasLimit,
                toAddress,
                BigInteger.ZERO,
                message
            )
            // Sign and send the transaction
            val signedTransaction: ByteArray = TransactionEncoder.signMessage(rawTransaction, credentials)
            val hexValue: String = Numeric.toHexString(signedTransaction)

            val transactionHash: String = web3j.ethSendRawTransaction(hexValue).send().transactionHash
            Log.i(TAG, "transfer2: $transactionHash")
//            delay(1000)
//            val receipt1 = web3j.ethGetTransactionReceipt(transactionHash).send().transactionReceipt

//            val receipt: TransactionReceipt = web3j.ethGetTransactionReceipt(transactionHash).sendAsync().get().transactionReceipt.get()
//            delay(5000)


            // get ethGetTransactionReceipt
            // Continuously poll for the transaction receipt
            var receipt: TransactionReceipt? = null
            while (receipt == null) {
                receipt = web3j.ethGetTransactionReceipt(transactionHash).send().transactionReceipt.orElse(null)
                delay(2000)
            }
            // Call the provided callback with the transaction receipt on the main thread
            withContext(Dispatchers.Main) {
                callback(receipt)
            }
        }
    }



}
