package net.devh.springboot.autoconfigure.grpc.server.ratelimit;


import com.ecwid.consul.v1.ConsulClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;

import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.RateLimiter;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.generator.RateLimitKeyGenerator;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.properties.RateLimitProperties;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.repository.ConsulRateLimiter;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.repository.InMemoryRateLimiter;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.grpc.GrpcRateLimitKeyGenerator;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.zuul.RateLimitPostFilter;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.zuul.RateLimitPreFilter;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.zuul.ZuulRateLimitKeyGenerator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UrlPathHelper;

import static net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.properties.RateLimitProperties.PREFIX;

@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
@ConditionalOnProperty(prefix = PREFIX, name = "enabled", havingValue = "true")
public class RateLimitAutoConfiguration {


  @ConditionalOnProperty(prefix = PREFIX, name = "server", havingValue = "ZUUL")
  public static class ZuulConfiguration {

    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Bean
    public ZuulFilter rateLimiterPreFilter(final RateLimiter rateLimiter,
                                           final RateLimitProperties rateLimitProperties,
                                           final RouteLocator routeLocator,
                                           final RateLimitKeyGenerator rateLimitKeyGenerator) {
      return new RateLimitPreFilter(rateLimitProperties, routeLocator, urlPathHelper, rateLimiter,
              rateLimitKeyGenerator);
    }

    @Bean
    public ZuulFilter rateLimiterPostFilter(final RateLimiter rateLimiter,
                                            final RateLimitProperties rateLimitProperties,
                                            final RouteLocator routeLocator,
                                            final RateLimitKeyGenerator rateLimitKeyGenerator) {
      return new RateLimitPostFilter(rateLimitProperties, routeLocator, urlPathHelper, rateLimiter,
              rateLimitKeyGenerator);
    }

    @Bean
    @ConditionalOnMissingBean(RateLimitKeyGenerator.class)
    public RateLimitKeyGenerator ratelimitKeyGenerator(final RateLimitProperties properties) {
      return new ZuulRateLimitKeyGenerator(properties);
    }

  }


  @ConditionalOnProperty(prefix = PREFIX, name = "server", havingValue = "GRPC")
  public static class GrpcConfiguration {

    @Bean
    @ConditionalOnMissingBean(RateLimitKeyGenerator.class)
    public RateLimitKeyGenerator ratelimitKeyGenerator(final RateLimitProperties properties) {
      return new GrpcRateLimitKeyGenerator(properties);
    }
  }

  @ConditionalOnMissingBean(RateLimiter.class)
  public static class RepositoryConfiguration {

    @Bean
    @ConditionalOnConsulEnabled
    @ConditionalOnProperty(prefix = PREFIX, name = "repository", havingValue = "CONSUL")
    public RateLimiter consultRateLimiter(final ConsulClient consulClient, final ObjectMapper objectMapper) {
      return new ConsulRateLimiter(consulClient, objectMapper);
    }

    @Bean
    @ConditionalOnProperty(prefix = PREFIX, name = "repository", havingValue = "IN_MEMORY", matchIfMissing = true)
    public RateLimiter inMemoryRateLimiter() {
      return new InMemoryRateLimiter();
    }

  }

}
