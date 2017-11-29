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
import com.netflix.zuul.exception.ZuulException;

import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.Rate;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.RateLimiter;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.generator.RateLimitKeyGenerator;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.properties.RateLimitProperties;

import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.FORM_BODY_WRAPPER_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

public class RateLimitPreFilter extends AbstractRateLimitFilter {

  private final RateLimiter rateLimiter;
  private final RateLimitKeyGenerator rateLimitKeyGenerator;

  public RateLimitPreFilter(final RateLimitProperties properties, final RouteLocator routeLocator,
                            final UrlPathHelper urlPathHelper, final RateLimiter rateLimiter,
                            final RateLimitKeyGenerator rateLimitKeyGenerator) {
    super(properties, routeLocator, urlPathHelper);
    this.rateLimiter = rateLimiter;
    this.rateLimitKeyGenerator = rateLimitKeyGenerator;
  }

  @Override
  public String filterType() {
    return PRE_TYPE;
  }

  @Override
  public int filterOrder() {
    return FORM_BODY_WRAPPER_FILTER_ORDER;
  }

  @Override
  public Object run() {
    final RequestContext ctx = RequestContext.getCurrentContext();
    final HttpServletResponse response = ctx.getResponse();
    final HttpServletRequest request = ctx.getRequest();
    final Route route = route();

    policy(route).ifPresent(policy -> {
      final String key = rateLimitKeyGenerator.key(new ZuulCondition(request, route, policy));
      final Rate rate = rateLimiter.consume(policy, key, null);

      final Long limit = policy.getLimit();
      final Long remaining = rate.getRemaining();
      if (limit != null) {
        response.setHeader(LIMIT_HEADER, String.valueOf(limit));
        response.setHeader(REMAINING_HEADER, String.valueOf(Math.max(remaining, 0)));
      }

      final Long quota = policy.getQuota();
      final Long remainingQuota = rate.getRemainingQuota();
      if (quota != null) {
        RequestContextHolder.getRequestAttributes()
                .setAttribute(REQUEST_START_TIME, System.currentTimeMillis(), SCOPE_REQUEST);
        response.setHeader(QUOTA_HEADER, String.valueOf(quota));
        response.setHeader(REMAINING_QUOTA_HEADER,
                String.valueOf(MILLISECONDS.toSeconds(Math.max(remainingQuota, 0))));
      }

      response.setHeader(RESET_HEADER, String.valueOf(rate.getReset()));

      if ((limit != null && remaining < 0) || (quota != null && remainingQuota < 0)) {
        HttpStatus tooManyRequests = HttpStatus.TOO_MANY_REQUESTS;
        ctx.setResponseStatusCode(tooManyRequests.value());
        ctx.put("rateLimitExceeded", "true");
        ctx.setSendZuulResponse(false);
        ZuulException zuulException = new ZuulException(tooManyRequests.toString(), tooManyRequests.value(),
                null);
        throw new ZuulRuntimeException(zuulException);
      }
    });

    return null;
  }
}
