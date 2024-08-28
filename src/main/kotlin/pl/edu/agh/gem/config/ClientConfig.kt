package pl.edu.agh.gem.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import pl.edu.agh.gem.helper.http.GemRestTemplateFactory
import java.time.Duration

@Configuration
class ClientConfig {
    @Bean
    @Qualifier("CurrencyManagerRestTemplate")
    fun currencyManagerRestTemplate(
        currencyManagerProperties: CurrencyManagerProperties,
        gemRestTemplateFactory: GemRestTemplateFactory,
    ): RestTemplate {
        return gemRestTemplateFactory
            .builder()
            .withReadTimeout(currencyManagerProperties.readTimeout)
            .withConnectTimeout(currencyManagerProperties.connectTimeout)
            .build()
    }

    @Bean
    @Qualifier("AttachmentStoreRestTemplate")
    fun attachmentStoreRestTemplate(
        attachmentStoreProperties: AttachmentStoreProperties,
        gemRestTemplateFactory: GemRestTemplateFactory,
    ): RestTemplate {
        return gemRestTemplateFactory
            .builder()
            .withReadTimeout(attachmentStoreProperties.readTimeout)
            .withConnectTimeout(attachmentStoreProperties.connectTimeout)
            .build()
    }

    @Bean
    @Qualifier("FinanceAdapterRestTemplate")
    fun financeAdapterRestTemplate(
        financeAdapterProperties: FinanceAdapterProperties,
        gemRestTemplateFactory: GemRestTemplateFactory,
    ): RestTemplate {
        return gemRestTemplateFactory
            .builder()
            .withReadTimeout(financeAdapterProperties.readTimeout)
            .withConnectTimeout(financeAdapterProperties.connectTimeout)
            .build()
    }
}

@ConfigurationProperties(prefix = "currency-manager")
data class CurrencyManagerProperties(
    val url: String,
    val connectTimeout: Duration,
    val readTimeout: Duration,
)

@ConfigurationProperties(prefix = "attachment-store")
data class AttachmentStoreProperties(
    val url: String,
    val connectTimeout: Duration,
    val readTimeout: Duration,
)

@ConfigurationProperties(prefix = "finance-adapter")
data class FinanceAdapterProperties(
    val url: String,
    val connectTimeout: Duration,
    val readTimeout: Duration,
)
