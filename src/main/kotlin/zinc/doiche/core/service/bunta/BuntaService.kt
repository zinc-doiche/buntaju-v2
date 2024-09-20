package zinc.doiche.core.service.bunta

import com.mongodb.client.result.InsertOneResult
import org.bson.types.ObjectId
import zinc.doiche.core.domain.bunta.Bunta
import zinc.doiche.core.domain.bunta.BuntaMessage
import zinc.doiche.core.domain.bunta.BuntaUser

interface BuntaService {

    suspend fun getBunta(channelId: Long): Bunta?

    suspend fun getBunta(objectId: ObjectId): Bunta?

    suspend fun getBuntaUser(userId: Long): BuntaUser?

    suspend fun getBuntaUser(objectId: ObjectId): BuntaUser?

    suspend fun getBuntaMessage(messageId: Long): BuntaMessage?

    suspend fun getBuntaMessage(objectId: ObjectId): BuntaMessage?

    suspend fun getBuntaMessageListOfBunta(bunta: Bunta, limit: Int): List<BuntaMessage>

    suspend fun saveBunta(bunta: Bunta): InsertOneResult

    suspend fun saveBuntaUser(buntaUser: BuntaUser): InsertOneResult

    suspend fun saveBuntaMessage(buntaMessage: BuntaMessage): InsertOneResult
}