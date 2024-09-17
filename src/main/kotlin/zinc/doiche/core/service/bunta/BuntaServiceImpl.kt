package zinc.doiche.core.service.bunta

import com.mongodb.client.result.InsertOneResult
import org.bson.types.ObjectId
import zinc.doiche.core.collector.BuntaCollector
import zinc.doiche.core.collector.BuntaMessageCollector
import zinc.doiche.core.collector.BuntaUserCollector
import zinc.doiche.core.domain.Bunta
import zinc.doiche.core.domain.BuntaMessage
import zinc.doiche.core.domain.BuntaUser
import zinc.doiche.lib.util.toObject

class BuntaServiceImpl(
    private val buntaCollector: BuntaCollector,
    private val buntaUserCollector: BuntaUserCollector,
    private val buntaMessageCollector: BuntaMessageCollector
) : BuntaService {
    override suspend fun getBunta(channelId: Long): Bunta? {
        return buntaCollector.findOneById(channelId)
    }

    override suspend fun getBunta(objectId: ObjectId): Bunta? {
        return buntaCollector.findOne(objectId)?.toObject(Bunta::class.java)
    }

    override suspend fun getBuntaUser(userId: Long): BuntaUser? {
        return buntaUserCollector.findOneById(userId)
    }

    override suspend fun getBuntaUser(objectId: ObjectId): BuntaUser? {
        return buntaUserCollector.findOne(objectId)?.toObject(BuntaUser::class.java)
    }

    override suspend fun getBuntaMessage(messageId: Long): BuntaMessage? {
        return buntaMessageCollector.findOneById(messageId)
    }

    override suspend fun getBuntaMessage(objectId: ObjectId): BuntaMessage? {
        return buntaMessageCollector.findOne(objectId)?.toObject(BuntaMessage::class.java)
    }

    override suspend fun getBuntaMessageListOfBunta(bunta: Bunta, limit: Int): List<BuntaMessage> {
        return buntaMessageCollector.findMany(bunta.objectId, limit)
    }

    override suspend fun saveBunta(bunta: Bunta): InsertOneResult {
        return buntaCollector.insertOne(bunta)
    }

    override suspend fun saveBuntaUser(buntaUser: BuntaUser): InsertOneResult {
        return buntaUserCollector.insertOne(buntaUser)
    }

    override suspend fun saveBuntaMessage(buntaMessage: BuntaMessage): InsertOneResult {
        return buntaMessageCollector.insertOne(buntaMessage)
    }
}