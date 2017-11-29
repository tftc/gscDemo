package net.devh.springboot.autoconfigure.grpc.server.ratelimit.config;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rate {

  private String key;
  private Long remaining;
  private Long remainingQuota;
  private Long reset;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
  private Date expiration;

}