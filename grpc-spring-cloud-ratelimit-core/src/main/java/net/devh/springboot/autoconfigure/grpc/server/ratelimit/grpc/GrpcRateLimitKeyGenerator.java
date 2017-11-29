package net.devh.springboot.autoconfigure.grpc.server.ratelimit.grpc;

import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.generator.RateLimitCondition;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.generator.RateLimitKeyGenerator;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.properties.RateLimitProperties;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.properties.RateLimitProperties.Policy;

import java.util.StringJoiner;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GrpcRateLimitKeyGenerator implements RateLimitKeyGenerator {

  private final RateLimitProperties properties;

  @Override
  public String key(RateLimitCondition condition) {
    GrpcCondition cond = (GrpcCondition) condition;
    Policy policy = cond.getPolicy();
    final StringJoiner joiner = new StringJoiner(":");
    joiner.add(properties.getKeyPrefix());
    joiner.add(cond.getMethodName());
    return joiner.toString();
  }
}