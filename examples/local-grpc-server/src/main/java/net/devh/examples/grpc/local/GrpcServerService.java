package net.devh.examples.grpc.local;


import com.onedata.demo.service.FooService;

import net.devh.examples.grpc.lib.HelloReply;
import net.devh.examples.grpc.lib.HelloRequest;
import net.devh.examples.grpc.lib.SimpleGrpc;
import net.devh.springboot.autoconfigure.grpc.server.GrpcService;

import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.stub.StreamObserver;

/**
 * User: Michael Email: yidongnan@gmail.com Date: 2016/11/8
 */

@GrpcService(SimpleGrpc.class)
public class GrpcServerService extends SimpleGrpc.SimpleImplBase {

  @Autowired
  FooService fooService;

  @Override
  public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
    HelloReply reply = HelloReply.newBuilder().setMessage("Hello =============> " + req.getName()).build();
    responseObserver.onNext(reply);
    responseObserver.onCompleted();
    fooService.testFind();
  }
}
