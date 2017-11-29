package net.devh.springboot.autoconfigure.grpc.server;

import com.google.common.collect.Lists;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import io.grpc.ServerInterceptor;
import lombok.Getter;

@Getter
public class GlobalServerInterceptorRegistry implements ApplicationContextAware {

  private final List<ServerInterceptor> serverInterceptors = Lists.newArrayList();
  private ApplicationContext applicationContext;

  @PostConstruct
  public void init() {
    //加入StatusInterceptor，最后一次
    serverInterceptors.add(new StatusServerInterceptor());
    Map<String, GlobalServerInterceptorConfigurerAdapter> map = applicationContext.getBeansOfType(GlobalServerInterceptorConfigurerAdapter.class);
    for (GlobalServerInterceptorConfigurerAdapter globalServerInterceptorConfigurerAdapter : map.values()) {
      globalServerInterceptorConfigurerAdapter.addServerInterceptors(this);
    }
  }

  public GlobalServerInterceptorRegistry addServerInterceptors(ServerInterceptor interceptor) {
    serverInterceptors.add(interceptor);
    return this;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}