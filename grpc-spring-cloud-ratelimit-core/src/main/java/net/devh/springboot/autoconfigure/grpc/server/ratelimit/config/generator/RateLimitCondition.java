package net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.generator;

import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.properties.RateLimitProperties.Policy;

public interface RateLimitCondition {
  Policy getPolicy();
}
