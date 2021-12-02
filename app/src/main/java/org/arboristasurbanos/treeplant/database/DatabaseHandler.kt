package org.arboristasurbanos.treeplant.database

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteException
import org.arboristasurbanos.treeplant.model.TreeModelClass

class DatabaseHandler(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "TreeDatabase"
        private val TABLE_CONTACTS = "TreeTable"
        private val KEY_ID = "id"
        private val KEY_NAME = "name"
        private val KEY_DATE = "date"
        private val KEY_LOCLAT = "lat"
        private val KEY_LOCLONG = "long"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //creating table with fields
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT,"
                + KEY_DATE + " TEXT," + KEY_LOCLAT + " REAL, " + KEY_LOCLONG + " REAL)")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS)
        onCreate(db)
    }


    //method to insert data
    fun addTree(tree: TreeModelClass):Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, tree.Name)
        contentValues.put(KEY_DATE, tree.Date )
        contentValues.put(KEY_LOCLAT, tree.Lat )
        contentValues.put(KEY_LOCLONG, tree.Long )
        // Inserting Row
        val success = db.insert(TABLE_CONTACTS, null, contentValues)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
    //method to read data
    @SuppressLint("Range")
    fun viewTree():List<TreeModelClass>{
        val treeList:ArrayList<TreeModelClass> = ArrayList<TreeModelClass>()
        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var treeId: Int
        var treeName: String
        var treeDate: String
        var treeLat: Double
        var treeLong: Double

        if (cursor.moveToFirst()) {
            do {
                treeId = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                treeName = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                treeDate = cursor.getString(cursor.getColumnIndex(KEY_DATE))
                treeLat = cursor.getDouble(cursor.getColumnIndex(KEY_LOCLAT))
                treeLong = cursor.getDouble(cursor.getColumnIndex(KEY_LOCLONG))
                val tree= TreeModelClass(Id = treeId, Name = treeName, Date = treeDate, Lat = treeLat, Long = treeLong)
                treeList.add(tree)
            } while (cursor.moveToNext())
        }
        return treeList
    }
    //method to update data
    fun updateEmployee(tree: TreeModelClass):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, tree.Id)
        contentValues.put(KEY_NAME, tree.Name)
        contentValues.put(KEY_DATE,tree.Date ) // EmpModelClass Email

        // Updating Row
        val success = db.update(TABLE_CONTACTS, contentValues,"id="+tree.Id,null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
    //method to delete data
    fun deleteTree(Id: Int):Boolean{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, Id)
        // Deleting Row
        val success = db.delete(TABLE_CONTACTS,"id="+Id,null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return (success>0)
    }
}