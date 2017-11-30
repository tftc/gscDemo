package net.devh.springboot.autoconfigure.grpc.server.ratelimit.grpc;

import java.util.Optional;

import io.grpc.ForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;

import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.Rate;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.generator.RateLimitKeyGenerator;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.RateLimiter;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.properties.RateLimitProperties;
import net.devh.springboot.autoconfigure.grpc.server.ratelimit.config.properties.RateLimitProperties.Policy;

@Slf4j
public class RateLimiterServerInterceptor implements ServerInterceptor {

  RateLimitKeyGenerator rateLimitKeyGenerator;
  RateLimiter rateLimiter;
  RateLimitProperties properties;

  public RateLimiterServerInterceptor(RateLimitKeyGenerator rateLimitKeyGenerator, RateLimiter rateLimiter, RateLimitProperties properties) {
    this.rateLimitKeyGenerator = rateLimitKeyGenerator;
    this.rateLimiter = rateLimiter;
    this.properties = properties;
  }

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
    final long start = System.currentTimeMillis();
    String methodName = call.getMethodDescriptor().getFullMethodName();
    log.info("RateLimiterServerInterceptor start ...");
    Optional<Policy> policy_o = properties.getPolicy(methodName);
    final boolean limitFlag = policy_o.isPresent();
    if (limitFlag) {
      Policy policy = policy_o.get();
      final String key = rateLimitKeyGenerator.key(new GrpcCondition(policy, methodName));
      final Rate rate = rateLimiter.consume(policy, key, null);

      final Long limit = policy.getLimit();
      final Long remaining = rate.getRemaining();

      final Long quota = policy.getQuota();
      final Long remainingQuota = rate.getRemainingQuota();

      if ((limit != null && remaining < 0) || (quota != null && remainingQuota < 0)) {
        headers.put(Metadata.Key.of("ratelimit", Metadata.ASCII_STRING_MARSHALLER), "close");
      }
    }
    return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {

      @SuppressWarnings("ConstantConditions")
      @Override
      public void close(Status status, Metadata trailers) {
        if(limitFlag) {
          Policy policy = policy_o.get();
          final String key = rateLimitKeyGenerator.key(new GrpcCondition(policy, methodName));
          final long requestTime = System.currentTimeMillis() - start;
          rateLimiter.consume(policy, key, requestTime);
        }
        try {
          super.close(status, trailers);
        } catch (Throwable t) {
          log.warn("call had closed, msg:{}", t.getMessage());
        }
        log.info("RateLimiterServerInterceptor end !");
      }
    }, headers);
  }
}
