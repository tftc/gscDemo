package net.devh.examples.grpc.local;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * User: Michael Email: yidongnan@gmail.com Date: 2016/11/8
 */
@ImportResource("applicationContext.xml")
@SpringBootApplication
public class GrpcServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(GrpcServerApplication.class, args);
  }
}
