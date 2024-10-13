package zinc.doiche.lib.inject

import com.github.benmanes.caffeine.cache.Cache
import net.dv8tion.jda.api.JDA
import org.bson.types.ObjectId
import org.slf4j.Logger
import zinc.doiche.core.collector.BuntaCollector
import zinc.doiche.core.collector.BuntaMessageCollector
import zinc.doiche.core.collector.BuntaUserCollector
import zinc.doiche.core.domain.bunta.BuntaUser
import zinc.doiche.lib.init.ApplicationContext
import zinc.doiche.lib.init.annotation.Inject
import zinc.doiche.lib.init.annotation.Injectable
import zinc.doiche.lib.init.annotation.InjectionOrder
import zinc.doiche.lib.init.annotation.Injector

@Injectable
class CollectorInjections {

    @Injector
    @InjectionOrder(1)
    fun getBuntaCollector(applicationContext: ApplicationContext): BuntaCollector {
        return BuntaCollector(applicationContext.getDatabase(), "Bunta")
    }

    @Injector
    @InjectionOrder(1)
    fun getBuntaUserCollector(applicationContext: ApplicationContext, jda: JDA): BuntaUserCollector {
        return BuntaUserCollector(applicationContext.getDatabase(), "BuntaUser", jda)
    }

    @Injector
    @InjectionOrder(1)
    fun getBuntaMessageCollector(
        applicationContext: ApplicationContext,
        @Inject("buntaUserCache") buntaUserCache: Cache<ObjectId, BuntaUser>,
        jda: JDA,
        logger: Logger
    ): BuntaMessageCollector {
        return BuntaMessageCollector(applicationContext.getDatabase(), "BuntaMessage", buntaUserCache, jda, logger)
    }
}