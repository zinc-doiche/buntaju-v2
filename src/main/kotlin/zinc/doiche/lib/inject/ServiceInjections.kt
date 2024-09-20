package zinc.doiche.lib.inject

import retrofit2.Retrofit
import zinc.doiche.core.collector.BuntaCollector
import zinc.doiche.core.collector.BuntaMessageCollector
import zinc.doiche.core.collector.BuntaUserCollector
import zinc.doiche.core.service.openai.OpenAIService
import zinc.doiche.core.service.bunta.BuntaService
import zinc.doiche.core.service.bunta.BuntaServiceImpl
import zinc.doiche.lib.init.annotation.Injectable
import zinc.doiche.lib.init.annotation.InjectionOrder
import zinc.doiche.lib.init.annotation.Injector

@Injectable
class ServiceInjections {

    @Injector
    @InjectionOrder(2)
    fun getOpenAIService(retrofit: Retrofit): OpenAIService {
        return retrofit.create(OpenAIService::class.java)
    }

    @Injector
    @InjectionOrder(2)
    fun getBuntaService(
        buntaCollector: BuntaCollector,
        buntaUserCollector: BuntaUserCollector,
        buntaMessageCollector: BuntaMessageCollector
    ): BuntaService {
        return BuntaServiceImpl(buntaCollector, buntaUserCollector, buntaMessageCollector)
    }
}
