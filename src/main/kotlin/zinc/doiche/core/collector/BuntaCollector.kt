package zinc.doiche.core.collector

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import zinc.doiche.core.domain.bunta.Bunta
import zinc.doiche.lib.database.CRUDCollector
import zinc.doiche.lib.util.eq
import zinc.doiche.lib.util.toDocument
import zinc.doiche.lib.util.toObject
import zinc.doiche.lib.util.toSet

class BuntaCollector(
    override val mongoDatabase: MongoDatabase,
    override val collectionName: String
): CRUDCollector<Long, Bunta> {

    override suspend fun updateOne(bunta: Bunta): UpdateResult {
        return collection.updateOne(
            Bunta::channelId eq bunta.channelId,
            bunta.toDocument().toSet()
        )
    }

    override suspend fun deleteOne(id: Long): DeleteResult {
        return collection.deleteOne(Bunta::channelId eq id)
    }

    override suspend fun findOneById(id: Long): Bunta? {
        return collection.find(Bunta::channelId eq id)
            .firstOrNull()
            ?.toObject(Bunta::class.java)
    }
}