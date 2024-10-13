package zinc.doiche.core.collector

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import net.dv8tion.jda.api.JDA
import zinc.doiche.core.domain.bunta.BuntaUser
import zinc.doiche.lib.database.CRUDCollector
import zinc.doiche.lib.util.eq

class BuntaUserCollector(
    override val mongoDatabase: MongoDatabase,
    override val collectionName: String,
    private val jda: JDA
) : CRUDCollector<Long, BuntaUser> {
    override suspend fun findOneById(id: Long): BuntaUser? {
        return getCollection().find(BuntaUser::userId eq id).firstOrNull()
    }

    override suspend fun updateOne(buntaUser: BuntaUser): UpdateResult {
        return getCollection().replaceOne(BuntaUser::userId eq buntaUser.userId, buntaUser)
    }

    override suspend fun deleteOne(id: Long): DeleteResult {
        return getCollection().deleteOne(BuntaUser::userId eq id)
    }

    override fun getCollection(): MongoCollection<BuntaUser> {
        return mongoDatabase.getCollection(collectionName)
    }

    suspend fun getAIUser(): BuntaUser? {
        val idLong = jda.selfUser.idLong
        return getCollection().find(BuntaUser::userId eq idLong).firstOrNull()
    }
}