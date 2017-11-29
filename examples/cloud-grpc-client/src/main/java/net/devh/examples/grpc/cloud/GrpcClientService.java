package net.devh.examples.grpc.cloud;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import net.devh.examples.grpc.lib.HelloReply;
import net.devh.examples.grpc.lib.HelloRequest;
import net.devh.examples.grpc.lib.SimpleGrpc;
import net.devh.springboot.autoconfigure.grpc.client.GrpcClient;

import org.springframework.stereotype.Service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GrpcClientService {

  @GrpcClient(value = "cloud-grpc-server", clazz = SimpleGrpc.SimpleBlockingStub.class)
  private SimpleGrpc.SimpleBlockingStub simpleStub;

  @SneakyThrows
  @HystrixCommand(fallbackMethod = "defaultFallback")
  public String sendMessage(String name) {
    try {
      HelloReply response = simpleStub.sayHello(HelloRequest.newBuilder().setName(name).build());
      return response.getMessage();
    } catch (Throwable t) {
      log.error("xxx", t);
      throw t;
    }
  }

  public String defaultFallback(String name) {
    return "hello:" + name;
  }
}
