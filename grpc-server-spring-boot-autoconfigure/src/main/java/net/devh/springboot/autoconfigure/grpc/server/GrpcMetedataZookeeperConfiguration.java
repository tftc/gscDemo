package net.devh.springboot.autoconfigure.grpc.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.zookeeper.discovery.ZookeeperDiscoveryProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@EnableConfigurationProperties
@ConditionalOnBean(ZookeeperDiscoveryProperties.class)
public class GrpcMetedataZookeeperConfiguration {

  @Autowired
  private ZookeeperDiscoveryProperties instance;

  @Autowired
  private GrpcServerProperties grpcProperties;

  @PostConstruct
  public void init() {
    this.instance.getMetadata().put("gRPC", String.valueOf(grpcProperties.getPort()));
  }
}