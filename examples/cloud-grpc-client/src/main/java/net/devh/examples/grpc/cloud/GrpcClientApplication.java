package net.devh.examples.grpc.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableDiscoveryClient
@EnableZuulProxy
@EnableCircuitBreaker
@SpringBootApplication
public class GrpcClientApplication {

  public static void main(String[] args) {
    SpringApplication.run(GrpcClientApplication.class, args);
  }
}
