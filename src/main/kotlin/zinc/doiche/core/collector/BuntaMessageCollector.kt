package zinc.doiche.core.collector

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId
import zinc.doiche.core.domain.bunta.BuntaMessage
import zinc.doiche.lib.database.CRUDCollector
import zinc.doiche.lib.util.eq
import zinc.doiche.lib.util.toDocument
import zinc.doiche.lib.util.toObject
import zinc.doiche.lib.util.toSet

class BuntaMessageCollector(
    override val mongoDatabase: MongoDatabase,
    override val collectionName: String
) : CRUDCollector<Long, BuntaMessage> {
    override suspend fun findOneById(id: Long): BuntaMessage? {
        return collection.find(BuntaMessage::messageId eq id)
            .firstOrNull()
            ?.toObject(BuntaMessage::class.java)
    }

    override suspend fun updateOne(t: BuntaMessage): UpdateResult {
        return collection.updateOne(
            BuntaMessage::messageId eq t.messageId,
            t.toDocument().toSet()
        )
    }

    override suspend fun deleteOne(id: Long): DeleteResult {
        return collection.deleteOne(BuntaMessage::messageId eq id)
    }

    suspend fun findMany(objectId: ObjectId, limit: Int): List<BuntaMessage> {
        return collection.find(BuntaMessage::channelObjectId eq objectId)
            .limit(limit)
            .map {
                it.toObject(BuntaMessage::class.java)
            }
            .toList()
    }
}