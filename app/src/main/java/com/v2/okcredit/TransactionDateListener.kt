package com.v2.okcredit

import com.v2.okcredit.model.Transaction

interface TransactionDateListener {
    fun getDate(transaction: Transaction)
}