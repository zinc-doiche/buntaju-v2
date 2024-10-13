package zinc.doiche.core.collector

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import zinc.doiche.core.domain.bunta.Bunta
import zinc.doiche.lib.database.CRUDCollector
import zinc.doiche.lib.util.eq

class BuntaCollector(
    override val mongoDatabase: MongoDatabase,
    override val collectionName: String
): CRUDCollector<Long, Bunta> {

    override fun getCollection(): MongoCollection<Bunta> {
        return mongoDatabase.getCollection(collectionName)
    }

    override suspend fun updateOne(bunta: Bunta): UpdateResult {
        return getCollection().replaceOne(Bunta::channelId eq bunta.channelId, bunta)
    }

    override suspend fun deleteOne(id: Long): DeleteResult {
        return getCollection().deleteOne(Bunta::channelId eq id)
    }

    override suspend fun findOneById(id: Long): Bunta? {
        return getCollection().find(Bunta::channelId eq id).firstOrNull()
    }
}