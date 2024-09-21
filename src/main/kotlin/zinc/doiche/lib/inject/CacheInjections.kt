package zinc.doiche.lib.inject

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.bson.types.ObjectId
import zinc.doiche.core.domain.bunta.Bunta
import zinc.doiche.core.domain.bunta.BuntaMessage
import zinc.doiche.core.domain.bunta.BuntaUser
import zinc.doiche.lib.init.annotation.Injectable
import zinc.doiche.lib.init.annotation.Injector
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Injectable
class CacheInjections {

    @Injector("buntaCache")
    fun buntaCache(): Cache<ObjectId, Bunta> = Caffeine
        .newBuilder()
        .expireAfterWrite(100.seconds.toJavaDuration())
        .maximumSize(100)
        .build()

    @Injector("buntaUserCache")
    fun buntaUserCache(): Cache<ObjectId, BuntaUser> = Caffeine
        .newBuilder()
        .expireAfterWrite(100.seconds.toJavaDuration())
        .maximumSize(100)
        .build()

    @Injector("buntaMessageCache")
    fun buntaMessageCache(): Cache<ObjectId, BuntaMessage> = Caffeine
        .newBuilder()
        .expireAfterWrite(100.seconds.toJavaDuration())
        .maximumSize(100)
        .build()
}