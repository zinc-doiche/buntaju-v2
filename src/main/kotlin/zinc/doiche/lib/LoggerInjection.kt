package zinc.doiche.lib

import net.dv8tion.jda.internal.utils.JDALogger
import org.slf4j.Logger
import zinc.doiche.lib.init.annotation.Injectable
import zinc.doiche.lib.init.annotation.Injector

@Injectable
class LoggerInjection {

    @Injector
    fun getLogger(): Logger {
        return JDALogger.getLog(javaClass)
    }
}