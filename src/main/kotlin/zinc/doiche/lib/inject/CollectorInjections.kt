package zinc.doiche.lib.inject

import zinc.doiche.core.collector.BuntaCollector
import zinc.doiche.core.collector.BuntaMessageCollector
import zinc.doiche.core.collector.BuntaUserCollector
import zinc.doiche.lib.init.ApplicationContext
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
    fun getBuntaUserCollector(applicationContext: ApplicationContext): BuntaUserCollector {
        return BuntaUserCollector(applicationContext.getDatabase(), "BuntaUser")
    }

    @Injector
    @InjectionOrder(1)
    fun getBuntaMessageCollector(applicationContext: ApplicationContext): BuntaMessageCollector {
        return BuntaMessageCollector(applicationContext.getDatabase(), "BuntaMessage")
    }
}