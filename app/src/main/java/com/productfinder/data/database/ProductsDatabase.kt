package com.productfinder.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.productfinder.models.Product

@Database(
    entities = [Product::class], version = 1, exportSchema = false
)
@TypeConverters(ProductsTypeConverter::class)
abstract class ProductsDatabase : RoomDatabase() {

    abstract fun productsDao(): ProductsDao

}