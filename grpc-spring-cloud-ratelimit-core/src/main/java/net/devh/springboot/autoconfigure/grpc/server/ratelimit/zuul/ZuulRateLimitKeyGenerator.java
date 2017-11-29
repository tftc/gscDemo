package net.devh.springboot.autoconfigure.grpc.server.ratelimit.zuul;

import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.generator.RateLimitCondition;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.generator.RateLimitKeyGenerator;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.properties.RateLimitProperties;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.properties.RateLimitProperties.Policy;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.properties.RateLimitProperties.Policy.Type;

import org.springframework.cloud.netflix.zuul.filters.Route;

import java.util.List;
import java.util.StringJoiner;

import javax.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.X_FORWARDED_FOR_HEADER;

@RequiredArgsConstructor
public class ZuulRateLimitKeyGenerator implements RateLimitKeyGenerator {

  private static final String ANONYMOUS_USER = "anonymous";

  private final RateLimitProperties properties;

  @Override
  public String key(RateLimitCondition condition) {
    ZuulCondition zuulCond = (ZuulCondition) condition;
    Policy policy = zuulCond.getPolicy();
    Route route = zuulCond.getRoute();
    HttpServletRequest request = zuulCond.getRequest();
    final List<Type> types = policy.getType();
    final StringJoiner joiner = new StringJoiner(":");
    joiner.add(properties.getKeyPrefix());
    if (route != null) {
      joiner.add(route.getId());
    }
    if (!types.isEmpty()) {
      if (types.contains(Type.URL) && route != null) {
        joiner.add(route.getPath());
      }
      if (types.contains(Type.ORIGIN)) {
        joiner.add(getRemoteAddress(request));
      }
      if (types.contains(Type.USER)) {
        joiner.add(request.getRemoteUser() != null ? request.getRemoteUser() : ANONYMOUS_USER);
      }
    }
    return joiner.toString();
  }

  private String getRemoteAddress(final HttpServletRequest request) {
    String xForwardedFor = request.getHeader(X_FORWARDED_FOR_HEADER);
    if (properties.isBehindProxy() && xForwardedFor != null) {
      return xForwardedFor;
    }
    return request.getRemoteAddr();
  }
}