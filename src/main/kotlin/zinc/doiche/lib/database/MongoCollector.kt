package zinc.doiche.lib.database

import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase

interface MongoCollector<T : Any> {

    val mongoDatabase: MongoDatabase

    val collectionName: String

    fun getCollection(): MongoCollection<T>
}