package net.devh.springboot.autoconfigure.grpc.server;

import net.devh.springboot.autoconfigure.grpc.server.ratelimit.RateLimitAutoConfiguration;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.RateLimiter;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.generator.RateLimitKeyGenerator;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.properties.RateLimitProperties;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.grpc.RateLimiterServerInterceptor;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(RateLimitAutoConfiguration.class)
@ConditionalOnBean(RateLimiter.class)
public class GrpcRateLimitAutoConfiguration {

  @Bean
  public GlobalServerInterceptorConfigurerAdapter rateLimitServerInterceptorConfigurerAdapter(final RateLimitKeyGenerator ratelimitKeyGenerator,
                                                                                              final RateLimiter rateLimiter,
                                                                                              final RateLimitProperties properties) {
    return new GlobalServerInterceptorConfigurerAdapter() {
      @Override
      public void addServerInterceptors(GlobalServerInterceptorRegistry registry) {
        registry.addServerInterceptors(new RateLimiterServerInterceptor(ratelimitKeyGenerator, rateLimiter, properties));
      }
    };
  }

}