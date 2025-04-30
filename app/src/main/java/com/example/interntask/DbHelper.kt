package com.example.interntask

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "details.db"
        private const val DATABASE_VERSION = 1

//        for the person description
        private const val TABLE_PROFILE = "Details"
        private const val COL_ID = "id"
        private const val COL_TITLE = "title"
        private const val COL_DESCRIPTION = "description"

//        for personal details
        private const val TABLE_PERSONAL = "PersonalDetails"
        private const val COL_PID = "id"
        private const val COL_NAME = "name"
        private const val COL_EMAIL = "email"
        private const val COL_LOCATION = "location"
        private const val COL_IMAGE = "image"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery =
            "CREATE TABLE $TABLE_PROFILE ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_TITLE TEXT, $COL_DESCRIPTION TEXT)"
        val createPersonalTable ="CREATE TABLE $TABLE_PERSONAL ($COL_PID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_NAME TEXT, $COL_EMAIL TEXT, $COL_LOCATION TEXT, $COL_IMAGE BLOB)"
        db?.execSQL(createPersonalTable)
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PROFILE")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PERSONAL")
        onCreate(db)
    }

    fun insertData(title: String, description: String): Long {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COL_TITLE, title)
            put(COL_DESCRIPTION, description)
        }
        val result = db.insert(TABLE_PROFILE, null, contentValues)
        db.close()
        return result
    }

    fun getAllUsers(): List<User> {
        val userList = mutableListOf<User>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PROFILE", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION))
                userList.add(User(id, title, description))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return userList
    }

    fun updateUser(id: Int, title: String, description: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", title)
            put("description", description)
        }
        val result = db.update("Details", values, "id=?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    fun deleteUser(id: Int): Boolean {
        val db = writableDatabase
        val result = db.delete("Details", "id=?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }
    fun insertPersonalDetails(name: String, email: String, location: String, selectedImage: ByteArray): Long {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put("name", name)
            put("email", email)
            put("location", location)
            put("image", selectedImage)
        }
        val result = db.insert("PersonalDetails", null, contentValues)
        db.close()
        return result
    }


    fun getPersonalDetails(): PersonalDetails? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PERSONAL WHERE $COL_PID = 1", null)
        var personal: PersonalDetails? = null

        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL))
            val location = cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION))
            val image = cursor.getBlob(cursor.getColumnIndexOrThrow(COL_IMAGE))

            personal = PersonalDetails(id, name, email, location, image)
        }

        cursor.close()
        db.close()
        return personal
    }

    fun updatePersonalDetails(updatedName: String, updatedEmail: String, updatedLocation: String, selectedImage: ByteArray): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", updatedName)
            put("email", updatedEmail)
            put("location", updatedLocation)
            put("image", selectedImage)
        }

        // Updating the row where id = 1 (assuming a single record exists)
        val result = db.update("PersonalDetails", values, "id = ?", arrayOf("1"))
        db.close()
        return result > 0
    }



}
