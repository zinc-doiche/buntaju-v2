package zinc.doiche.core.collector

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import zinc.doiche.core.domain.bunta.BuntaUser
import zinc.doiche.lib.database.CRUDCollector
import zinc.doiche.lib.util.eq
import zinc.doiche.lib.util.toDocument
import zinc.doiche.lib.util.toObject
import zinc.doiche.lib.util.toSet

class BuntaUserCollector(
    override val mongoDatabase: MongoDatabase,
    override val collectionName: String
) : CRUDCollector<Long, BuntaUser> {
    override suspend fun findOneById(id: Long): BuntaUser? {
        return collection.find(BuntaUser::userId eq id)
            .firstOrNull()
            ?.toObject(BuntaUser::class.java)
    }

    override suspend fun updateOne(t: BuntaUser): UpdateResult {
        return collection.updateOne(
            BuntaUser::userId eq t.userId,
            t.toDocument().toSet()
        )
    }

    override suspend fun deleteOne(id: Long): DeleteResult {
        return collection.deleteOne(BuntaUser::userId eq id)
    }
}