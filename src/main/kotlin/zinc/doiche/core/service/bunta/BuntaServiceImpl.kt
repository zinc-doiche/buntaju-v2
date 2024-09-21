package zinc.doiche.core.service.bunta

import com.github.benmanes.caffeine.cache.Cache
import com.mongodb.client.result.InsertOneResult
import org.bson.types.ObjectId
import zinc.doiche.core.collector.BuntaCollector
import zinc.doiche.core.collector.BuntaMessageCollector
import zinc.doiche.core.collector.BuntaUserCollector
import zinc.doiche.core.domain.bunta.Bunta
import zinc.doiche.core.domain.bunta.BuntaMessage
import zinc.doiche.core.domain.bunta.BuntaUser
import zinc.doiche.lib.util.toObject

class BuntaServiceImpl(
    private val buntaCollector: BuntaCollector,
    private val buntaUserCollector: BuntaUserCollector,
    private val buntaMessageCollector: BuntaMessageCollector,
    private val buntaCache: Cache<ObjectId, Bunta>,
    private val buntaUserCache: Cache<ObjectId, BuntaUser>,
    private val buntaMessageCache: Cache<ObjectId, BuntaMessage>,
) : BuntaService {
    override suspend fun getBunta(channelId: Long): Bunta? {
        return buntaCollector.findOneById(channelId)
    }

    override suspend fun getBunta(objectId: ObjectId): Bunta? {
        return buntaCache.getIfPresent(objectId) ?: run {
            buntaCollector.findOne(objectId)?.toObject(Bunta::class.java)?.apply {
                buntaCache.put(objectId, this)
            }
        }
    }

    override suspend fun getBuntaUser(userId: Long): BuntaUser? {
        return buntaUserCollector.findOneById(userId)
    }

    override suspend fun getBuntaUser(objectId: ObjectId): BuntaUser? {
        return buntaUserCache.getIfPresent(objectId) ?: run {
            buntaUserCollector.findOne(objectId)?.toObject(BuntaUser::class.java)?.apply {
                buntaUserCache.put(objectId, this)
            }
        }
    }

    override suspend fun getBuntaMessage(messageId: Long): BuntaMessage? {
        return buntaMessageCollector.findOneById(messageId)
    }

    override suspend fun getBuntaMessage(objectId: ObjectId): BuntaMessage? {
        return buntaMessageCache.getIfPresent(objectId) ?: run {
            buntaMessageCollector.findOne(objectId)?.toObject(BuntaMessage::class.java)?.apply {
                buntaMessageCache.put(objectId, this)
            }
        }
    }

    override suspend fun getBuntaMessageListOfBunta(bunta: Bunta, limit: Int): List<BuntaMessage> {
        return buntaMessageCollector.findMany(bunta.objectId, limit)
    }

    override suspend fun saveBunta(bunta: Bunta): InsertOneResult {
        val insertOneResult = buntaCollector.insertOne(bunta)
        buntaCache.put(bunta.objectId, bunta)
        return insertOneResult
    }

    override suspend fun saveBuntaUser(buntaUser: BuntaUser): InsertOneResult {
        val insertOneResult = buntaUserCollector.insertOne(buntaUser)
        buntaUserCache.put(buntaUser.objectId, buntaUser)
        return insertOneResult
    }

    override suspend fun saveBuntaMessage(buntaMessage: BuntaMessage): InsertOneResult {
        val insertOneResult = buntaMessageCollector.insertOne(buntaMessage)
        buntaMessageCache.put(buntaMessage.objectId, buntaMessage)
        return insertOneResult
    }
}