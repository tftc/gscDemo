package net.devh.springboot.autoconfigure.grpc.server.ratelimit.zuul;

import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.generator.RateLimitCondition;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.properties.RateLimitProperties.Policy;

import org.springframework.cloud.netflix.zuul.filters.Route;

import javax.servlet.http.HttpServletRequest;

import lombok.Data;

@Data
public class ZuulCondition implements RateLimitCondition {

  HttpServletRequest request;
  Route route;
  Policy policy;

  public ZuulCondition(HttpServletRequest request, Route route, Policy policy) {
    this.request = request;
    this.route = route;
    this.policy = policy;
  }

  @Override
  public Policy getPolicy() {
    return policy;
  }
}
