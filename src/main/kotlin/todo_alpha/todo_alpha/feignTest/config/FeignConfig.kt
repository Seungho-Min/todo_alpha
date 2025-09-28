package todo_alpha.todo_alpha.feignTest.config

import feign.Logger
import feign.Retryer
import feign.codec.ErrorDecoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
class FeignConfig {

    @Bean
    fun feignLoggerLevel(): Logger.Level = Logger.Level.FULL

    // 간단한 재시도: 최초 100ms, 최대 1초, 최대 3회
    @Bean
    fun feignRetryer(): Retryer = Retryer.Default(100, TimeUnit.SECONDS.toMillis(1), 3)

    @Bean
    fun feignErrorDecoder(): ErrorDecoder = ErrorDecoder.Default()
}