package zinc.doiche.core.service.bunta

import com.github.benmanes.caffeine.cache.Cache
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.JDA
import org.bson.types.ObjectId
import org.slf4j.Logger
import zinc.doiche.core.collector.BuntaCollector
import zinc.doiche.core.collector.BuntaMessageCollector
import zinc.doiche.core.collector.BuntaUserCollector
import zinc.doiche.core.domain.bunta.Bunta
import zinc.doiche.core.domain.bunta.BuntaMessage
import zinc.doiche.core.domain.bunta.BuntaUser
import zinc.doiche.core.`object`.MessageUnion
import zinc.doiche.core.`object`.UserUnion

class BuntaServiceImpl(
    private val buntaCollector: BuntaCollector,
    private val buntaUserCollector: BuntaUserCollector,
    private val buntaMessageCollector: BuntaMessageCollector,
    private val buntaCache: Cache<ObjectId, Bunta>,
    private val buntaUserCache: Cache<ObjectId, BuntaUser>,
    private val buntaMessageCache: Cache<ObjectId, BuntaMessage>,
    private val jda: JDA,
    private val logger: Logger
) : BuntaService {
    override suspend fun getBunta(channelId: Long): Bunta? {
        return buntaCollector.findOneById(channelId)
    }

    override suspend fun getBunta(objectId: ObjectId): Bunta? {
        return buntaCache.getIfPresent(objectId) ?: run {
            buntaCollector.findOne(objectId)?.apply {
                buntaCache.put(objectId, this)
            }
        }
    }

    override suspend fun getBuntaUser(userId: Long): BuntaUser? {
        return buntaUserCollector.findOneById(userId)
    }

    override suspend fun getBuntaUser(objectId: ObjectId): BuntaUser? {
        return buntaUserCache.getIfPresent(objectId) ?: run {
            buntaUserCollector.findOne(objectId)?.apply {
                buntaUserCache.put(objectId, this)
            }
        }
    }

    override suspend fun getBuntaMessage(messageId: Long): BuntaMessage? {
        return buntaMessageCollector.findOneById(messageId)
    }

    override suspend fun getBuntaMessage(objectId: ObjectId): BuntaMessage? {
        return buntaMessageCache.getIfPresent(objectId) ?: run {
            buntaMessageCollector.findOne(objectId)?.apply {
                buntaMessageCache.put(objectId, this)
            }
        }
    }

    override suspend fun getMessageUnionListOfBunta(bunta: Bunta, limit: Int): List<MessageUnion> {
        return buntaMessageCollector.findMany(bunta.objectId!!, limit).map { buntaMessage ->
            getBuntaUser(buntaMessage.senderObjectId)?.let { findUser ->
                val textChannel = jda.getTextChannelById(bunta.channelId) ?: return emptyList()
                val message = textChannel.retrieveMessageById(buntaMessage.messageId).submit().await()

                jda.retrieveUserById(findUser.userId).map { discordUser ->
                    MessageUnion(buntaMessage, message, UserUnion(discordUser, findUser))
                }.onErrorMap {
                    logger.warn("Failed to retrieve user: ", it)
                    null
                }.submit().await()
            } ?: return emptyList()
        }
    }

    override suspend fun updateBunta(bunta: Bunta): UpdateResult {
        val updateOne = buntaCollector.updateOne(bunta)
        buntaCache.put(bunta.objectId, bunta)
        return updateOne
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