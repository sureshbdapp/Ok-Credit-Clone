package com.v2.okcredit.room.dao

import androidx.room.*
import com.v2.okcredit.model.Customer

@Dao
interface CustomerDao {

    @Query("SELECT * from Customer")
    fun getCustomers(): MutableList<Customer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCustomer(customer: Customer)

    @Query("DELETE FROM Customer")
    fun deleteCustomer()

    @Update
    fun updateCustomer(customer: Customer) : Int
}