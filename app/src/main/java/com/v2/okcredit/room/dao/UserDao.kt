package com.v2.okcredit.room.dao

import androidx.room.*
import com.v2.okcredit.model.Customer
import com.v2.okcredit.model.User
import kotlin.Int as Int

@Dao
interface UserDao {

    @Query("SELECT * from User WHERE phone=:phone")
    fun getUser(phone: String): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Query("DELETE FROM User")
    fun delete()

    @Update
    fun updateUser(user: User): Int

    @Query("SELECT * from User")
    fun getALlUserDetails() : MutableList<User>
}