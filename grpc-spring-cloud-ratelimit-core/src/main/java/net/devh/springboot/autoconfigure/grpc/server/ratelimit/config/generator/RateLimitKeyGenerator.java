package net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.generator;

public interface RateLimitKeyGenerator {

  String key(RateLimitCondition condition);
}