package net.devh.springboot.autoconfigure.grpc.server;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StatusServerInterceptor implements ServerInterceptor {

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
    String ratelimit = headers.get(Metadata.Key.of("ratelimit", Metadata.ASCII_STRING_MARSHALLER));
    if ("close".equals(ratelimit)) {
      call.close(Status.UNAVAILABLE
              .withCause(new RuntimeException("ratelimit close"))
              .withDescription("ratelimit close"), headers);
    }
    return next.startCall(call, headers);
  }
}
