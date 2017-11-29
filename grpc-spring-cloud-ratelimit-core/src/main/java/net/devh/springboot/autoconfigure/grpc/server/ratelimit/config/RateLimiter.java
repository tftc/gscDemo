package net.devh.springboot.autoconfigure.grpc.server.ratelimit.config;

import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.properties.RateLimitProperties.Policy;

public interface RateLimiter {

  Rate consume(Policy policy, String key, Long requestTime);
}