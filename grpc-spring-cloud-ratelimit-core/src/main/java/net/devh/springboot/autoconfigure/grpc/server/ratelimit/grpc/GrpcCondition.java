package net.devh.springboot.autoconfigure.grpc.server.ratelimit.grpc;

import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.generator.RateLimitCondition;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.properties.RateLimitProperties.Policy;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GrpcCondition implements RateLimitCondition {

  private Policy policy;
  private String methodName;

  @Override
  public Policy getPolicy() {
    return policy;
  }
}
