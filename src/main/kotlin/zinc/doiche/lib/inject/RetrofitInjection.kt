package zinc.doiche.lib.inject

import net.dv8tion.jda.api.JDA
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import zinc.doiche.lib.init.annotation.Injectable
import zinc.doiche.lib.init.annotation.InjectionOrder
import zinc.doiche.lib.init.annotation.Injector

@Injectable
class RetrofitInjection {
    @Injector
    @InjectionOrder(1)
    fun getRetrofit(jda: JDA): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openai.com")
            .addConverterFactory(JacksonConverterFactory.create())
            .client(jda.httpClient)
            .build()
    }
}