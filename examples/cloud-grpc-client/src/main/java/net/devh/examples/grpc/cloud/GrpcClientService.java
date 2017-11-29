package net.devh.examples.grpc.cloud;

import net.devh.examples.grpc.lib.HelloReply;
import net.devh.examples.grpc.lib.HelloRequest;
import net.devh.examples.grpc.lib.SimpleGrpc;
import net.devh.springboot.autoconfigure.grpc.client.GrpcClient;

import org.springframework.stereotype.Service;

@Service
public class GrpcClientService {

  @GrpcClient(value = "cloud-grpc-server", clazz = SimpleGrpc.SimpleBlockingStub.class)
  private SimpleGrpc.SimpleBlockingStub simpleStub;

  public String sendMessage(String name) {
    HelloReply response = simpleStub.sayHello(HelloRequest.newBuilder().setName(name).build());
    return response.getMessage();
  }
}
