package zinc.doiche.lib.inject

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.benmanes.caffeine.cache.Cache
import net.dv8tion.jda.api.JDA
import org.bson.types.ObjectId
import org.slf4j.Logger
import retrofit2.Retrofit
import zinc.doiche.core.collector.BuntaCollector
import zinc.doiche.core.collector.BuntaMessageCollector
import zinc.doiche.core.collector.BuntaUserCollector
import zinc.doiche.core.domain.bunta.Bunta
import zinc.doiche.core.domain.bunta.BuntaMessage
import zinc.doiche.core.domain.bunta.BuntaUser
import zinc.doiche.core.requester.OpenAIRequester
import zinc.doiche.core.service.bunta.BuntaService
import zinc.doiche.core.service.bunta.BuntaServiceImpl
import zinc.doiche.core.service.openai.OpenAIService
import zinc.doiche.core.service.openai.OpenAIServiceImpl
import zinc.doiche.lib.init.Config
import zinc.doiche.lib.init.annotation.Inject
import zinc.doiche.lib.init.annotation.Injectable
import zinc.doiche.lib.init.annotation.InjectionOrder
import zinc.doiche.lib.init.annotation.Injector

@Injectable
class ServiceInjections {

    @Injector
    @InjectionOrder(1)
    fun getOpenAIRequester(retrofit: Retrofit): OpenAIRequester {
        return retrofit.create(OpenAIRequester::class.java)
    }

    @Injector
    @InjectionOrder(2)
    fun getOpenAIService(
        logger: Logger,
        config: Config,
        objectMapper: ObjectMapper,
        openAIRequester: OpenAIRequester,
        buntaUserCollector: BuntaUserCollector
    ): OpenAIService {
        return OpenAIServiceImpl(logger, config, objectMapper, openAIRequester, buntaUserCollector)
    }

    @Injector
    @InjectionOrder(2)
    fun getBuntaService(
        buntaCollector: BuntaCollector,
        buntaUserCollector: BuntaUserCollector,
        buntaMessageCollector: BuntaMessageCollector,
        @Inject("buntaCache") buntaCache: Cache<ObjectId, Bunta>,
        @Inject("buntaUserCache") buntaUserCache: Cache<ObjectId, BuntaUser>,
        @Inject("buntaMessageCache") buntaMessageCache: Cache<ObjectId, BuntaMessage>,
        jda: JDA,
        logger: Logger
    ): BuntaService {
        return BuntaServiceImpl(
            buntaCollector,
            buntaUserCollector,
            buntaMessageCollector,
            buntaCache,
            buntaUserCache,
            buntaMessageCache,
            jda,
            logger
        )
    }
}
