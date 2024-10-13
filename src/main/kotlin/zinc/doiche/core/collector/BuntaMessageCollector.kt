package zinc.doiche.core.collector

import com.github.benmanes.caffeine.cache.Cache
import com.mongodb.client.model.Sorts
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.*
import net.dv8tion.jda.api.JDA
import org.bson.types.ObjectId
import org.slf4j.Logger
import zinc.doiche.core.domain.bunta.BuntaMessage
import zinc.doiche.core.domain.bunta.BuntaUser
import zinc.doiche.lib.database.CRUDCollector
import zinc.doiche.lib.util.eq

class BuntaMessageCollector(
    override val mongoDatabase: MongoDatabase,
    override val collectionName: String,
    private val buntaUserCache: Cache<ObjectId, BuntaUser>,
    private val jda: JDA,
    private val logger: Logger
) : CRUDCollector<Long, BuntaMessage> {

    override suspend fun findOneById(id: Long): BuntaMessage? {
        return getCollection().find(BuntaMessage::messageId eq id).firstOrNull()
    }

    override suspend fun updateOne(buntaMessage: BuntaMessage): UpdateResult {
        return getCollection().replaceOne(BuntaMessage::messageId eq buntaMessage.messageId, buntaMessage)
    }

    override suspend fun deleteOne(id: Long): DeleteResult {
        return getCollection().deleteOne(BuntaMessage::messageId eq id)
    }

    suspend fun findMany(objectId: ObjectId, limit: Int): List<BuntaMessage> {
        return getCollection().find(BuntaMessage::channelObjectId eq objectId)
            .sort(Sorts.descending("_id"))
            .limit(limit)
            .sort(Sorts.ascending("_id"))
            .toList()
    }

    override fun getCollection(): MongoCollection<BuntaMessage> {
        return mongoDatabase.getCollection(collectionName)
    }
}