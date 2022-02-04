package org.arboristasurbanos.treeplant.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.location.LocationRequestCompat
import org.arboristasurbanos.treeplant.model.TreeModelClass
import java.io.ByteArrayOutputStream
import android.R.id





class DatabaseHandler(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION =  2
        private val DATABASE_NAME = "TreeDatabase"
        private val TREES_TABLE = "TreeTable"
        private val KEY_ID = "id"
        private val KEY_NAME = "name"
        private val KEY_DATE = "date"
        private val KEY_LOCLAT = "lat"
        private val KEY_LOCLONG = "long"

        private val PHOTO_TABLE = "TreePhoto"
        private val KEY_TREE_ID = "treeid"
        private val KEY_PHOTO = "photo"
        private val QUALITY = 85
    }
    override fun onCreate(db: SQLiteDatabase?) {
        //creating table with fields
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TREES_TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT,"
                + KEY_DATE + " TEXT," + KEY_LOCLAT + " REAL, " + KEY_LOCLONG + " REAL)")
        db?.execSQL(CREATE_CONTACTS_TABLE)
        CreatePhotoTable(db)
    }
    fun CreatePhotoTable(db: SQLiteDatabase?){
        val CREATE_PHOTO_TABLE = ("CREATE TABLE $PHOTO_TABLE ("
                + "$KEY_ID INTEGER NOT NULL PRIMARY KEY, $KEY_TREE_ID INTEGER, $KEY_PHOTO BLOB, " +
                "FOREIGN KEY ($KEY_TREE_ID) REFERENCES $TREES_TABLE($KEY_ID))")
        db?.execSQL(CREATE_PHOTO_TABLE)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion == 1 && newVersion == 2){
            CreatePhotoTable(db)
        }
        else {
            db!!.execSQL("DROP TABLE IF EXISTS " + TREES_TABLE)
            onCreate(db)
        }
    }


    //method to insert data
    fun addTree(tree: TreeModelClass): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, tree.Name)
        contentValues.put(KEY_DATE, tree.Date )
        contentValues.put(KEY_LOCLAT, tree.Lat )
        contentValues.put(KEY_LOCLONG, tree.Long )
        // Inserting Row
        val id = db.insert(TREES_TABLE, null, contentValues)
        if (tree.Photo != null && id > 0){
            val contentImgValue = ContentValues()
            contentImgValue.put(KEY_TREE_ID, id)
            val stream = ByteArrayOutputStream()
            tree.Photo!!.compress(Bitmap.CompressFormat.PNG, QUALITY, stream)
            val byteArray: ByteArray = stream.toByteArray()
            contentImgValue.put(KEY_PHOTO, byteArray)
            db.insert(PHOTO_TABLE, null, contentImgValue)
        }

        db.close() // Closing database connection
        return (id>0)
    }
    //method to read data
    @SuppressLint("Range")
    fun viewTrees():List<TreeModelClass>{
        val treeList:ArrayList<TreeModelClass> = ArrayList<TreeModelClass>()
        val selectQuery = "SELECT  * FROM $TREES_TABLE"
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
    @SuppressLint("Range")
    fun viewTree(id: Int): TreeModelClass? {
        val treeList:ArrayList<TreeModelClass> = ArrayList<TreeModelClass>()
        val selectQuery = "SELECT * FROM $TREES_TABLE LEFT JOIN $PHOTO_TABLE ON $TREES_TABLE.$KEY_ID = $PHOTO_TABLE.$KEY_TREE_ID WHERE $TREES_TABLE.id="+id
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return null
        }
        var treeId: Int
        var treeName: String
        var treeDate: String
        var treeLat: Double
        var treeLong: Double
        var Photo: Bitmap
        if (cursor.moveToFirst()) {
            treeId = cursor.getInt(cursor.getColumnIndex(KEY_ID))
            treeName = cursor.getString(cursor.getColumnIndex(KEY_NAME))
            treeDate = cursor.getString(cursor.getColumnIndex(KEY_DATE))
            treeLat = cursor.getDouble(cursor.getColumnIndex(KEY_LOCLAT))
            treeLong = cursor.getDouble(cursor.getColumnIndex(KEY_LOCLONG))
            var photoByteArray = cursor.getBlob(cursor.getColumnIndex(KEY_PHOTO))

            var tree= TreeModelClass(Id = treeId, Name = treeName, Date = treeDate, Lat = treeLat, Long = treeLong)
            if (photoByteArray != null) {
                Photo = BitmapFactory.decodeByteArray(photoByteArray, 0, photoByteArray.size);  var tree= TreeModelClass(Id = treeId, Name = treeName, Date = treeDate, Lat = treeLat, Long = treeLong, Photo = Photo)
                return TreeModelClass(Id = treeId, Name = treeName, Date = treeDate, Lat = treeLat, Long = treeLong, Photo = Photo)
            }
            return tree
        }
        return null
    }
    //method to update data
    fun updateTree(tree: TreeModelClass):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, tree.Id)
        contentValues.put(KEY_NAME, tree.Name)
        contentValues.put(KEY_DATE, tree.Date )
        if (tree.Lat != null)
            contentValues.put(KEY_LOCLAT,tree.Lat )
        if (tree.Long != null)
           contentValues.put(KEY_LOCLONG,tree.Long )
        if (tree.Photo != null){
            val photoValue = ContentValues()
            val stream = ByteArrayOutputStream()
            tree.Photo!!.compress(Bitmap.CompressFormat.PNG, QUALITY, stream)
            val byteArray: ByteArray = stream.toByteArray()
            photoValue.put(KEY_PHOTO, byteArray)
            photoValue.put(KEY_TREE_ID, tree.Id!!)
            // Updating Row
            db.insertOrUpdate(PHOTO_TABLE, photoValue, tree.Id!!, KEY_TREE_ID)
        }


        // Updating Row
        val success = db.update(TREES_TABLE, contentValues,"id="+tree.Id,null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
    fun updateTreeLocation(id: Int, Lat: Double, Long: Double ):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, id)
        contentValues.put(KEY_LOCLAT,Lat)
        contentValues.put(KEY_LOCLONG,Long)

        // Updating Row
        val success = db.update(TREES_TABLE, contentValues,"id="+id,null)
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
        val success = db.delete(TREES_TABLE,"id="+Id,null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return (success>0)
    }
}

private fun SQLiteDatabase.insertOrUpdate(tableName: String, values: ContentValues, Id: Int?, keyId:  String) {

    if (this.insertWithOnConflict(tableName, null, values, SQLiteDatabase.CONFLICT_IGNORE ).compareTo(-1) == 0 ) {
        this.update(
            tableName,
            values,
            keyId+"="+Id,
            null
        ) // number 1 is the _id here, update to variable for your code
    }
}
