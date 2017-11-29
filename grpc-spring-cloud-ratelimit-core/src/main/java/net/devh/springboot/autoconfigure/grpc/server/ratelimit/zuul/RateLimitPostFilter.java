/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.devh.springboot.autoconfigure.grpc.server.ratelimit.zuul;

import com.netflix.zuul.context.RequestContext;

import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.RateLimiter;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.generator.RateLimitKeyGenerator;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.properties.RateLimitProperties;

import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SEND_RESPONSE_FILTER_ORDER;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

public class RateLimitPostFilter extends AbstractRateLimitFilter {

  private final RateLimiter rateLimiter;
  private final RateLimitKeyGenerator rateLimitKeyGenerator;

  public RateLimitPostFilter(final RateLimitProperties properties, final RouteLocator routeLocator,
                             final UrlPathHelper urlPathHelper, final RateLimiter rateLimiter,
                             final RateLimitKeyGenerator rateLimitKeyGenerator) {
    super(properties, routeLocator, urlPathHelper);
    this.rateLimiter = rateLimiter;
    this.rateLimitKeyGenerator = rateLimitKeyGenerator;
  }

  @Override
  public String filterType() {
    return POST_TYPE;
  }

  @Override
  public int filterOrder() {
    return SEND_RESPONSE_FILTER_ORDER - 10;
  }

  @Override
  public boolean shouldFilter() {
    return super.shouldFilter() && getRequestStartTime() != null;
  }

  private Long getRequestStartTime() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    return (Long) requestAttributes.getAttribute(RateLimitPreFilter.REQUEST_START_TIME, SCOPE_REQUEST);
  }

  @Override
  public Object run() {
    final RequestContext ctx = RequestContext.getCurrentContext();
    final HttpServletRequest request = ctx.getRequest();
    final Route route = route();

    policy(route).ifPresent(policy -> {
      final Long requestTime = System.currentTimeMillis() - getRequestStartTime();
      final String key = rateLimitKeyGenerator.key(new ZuulCondition(request, route, policy));
      rateLimiter.consume(policy, key, requestTime);
    });

    return null;
  }
}
