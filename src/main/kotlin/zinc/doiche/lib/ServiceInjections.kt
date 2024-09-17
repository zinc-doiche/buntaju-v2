package zinc.doiche.lib

import zinc.doiche.zinc.doiche.applicationContext
import zinc.doiche.core.collector.BuntaCollector
import zinc.doiche.core.collector.BuntaMessageCollector
import zinc.doiche.core.collector.BuntaUserCollector
import zinc.doiche.core.service.openai.OpenAIService
import zinc.doiche.core.service.openai.OpenAIServiceImpl
import zinc.doiche.core.service.bunta.BuntaService
import zinc.doiche.core.service.bunta.BuntaServiceImpl
import zinc.doiche.lib.init.annotation.Injectable
import zinc.doiche.lib.init.annotation.Injector

@Injectable
class ServiceInjections {

    @Injector
    fun getOpenAIService(): OpenAIService {
        return OpenAIServiceImpl()
    }

    @Injector
    fun getBuntaService(): BuntaService {
        val mongoDatabase = applicationContext.mongoClient.getDatabase(applicationContext.config.database.getName())

        return BuntaServiceImpl(
            BuntaCollector(mongoDatabase, "Bunta"),
            BuntaUserCollector(mongoDatabase, "BuntaUser"),
            BuntaMessageCollector(mongoDatabase, "BuntaMessage")
        )
    }
}
