package zinc.doiche.lib.database

import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.bson.Document

interface MongoCollector {

    val mongoDatabase: MongoDatabase

    val collectionName: String

    val collection: MongoCollection<Document>
        get() = mongoDatabase.getCollection(collectionName)
}