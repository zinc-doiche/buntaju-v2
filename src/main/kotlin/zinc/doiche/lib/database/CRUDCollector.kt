package zinc.doiche.lib.database

import com.mongodb.client.model.Filters
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import kotlinx.coroutines.flow.firstOrNull
import org.bson.Document
import org.bson.types.ObjectId
import zinc.doiche.lib.util.toDocument

interface CRUDCollector<ID, T: Any> : MongoCollector {

    suspend fun insertOne(t: T): InsertOneResult {
        return collection.insertOne(t.toDocument())
    }

    suspend fun findOne(objectId: ObjectId): Document? {
        return collection.find(Filters.eq("_id", objectId)).firstOrNull();
    }

    suspend fun findOneById(id: ID): T?

    suspend fun updateOne(t: T): UpdateResult

    suspend fun deleteOne(id: ID): DeleteResult
}