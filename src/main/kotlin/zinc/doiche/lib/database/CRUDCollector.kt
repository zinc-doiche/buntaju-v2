package zinc.doiche.lib.database

import com.mongodb.client.model.Filters
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId

interface CRUDCollector<ID, T : Any> : MongoCollector<T> {

    suspend fun insertOne(t: T): InsertOneResult {
        return getCollection().insertOne(t)
    }

    suspend fun findOne(objectId: ObjectId): T? {
        return getCollection().find(Filters.eq("_id", objectId)).firstOrNull();
    }

    suspend fun findOneById(id: ID): T?

    suspend fun updateOne(t: T): UpdateResult

    suspend fun deleteOne(id: ID): DeleteResult
}