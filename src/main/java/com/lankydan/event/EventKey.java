package com.lankydan.event;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@PrimaryKeyClass
public class EventKey implements Serializable {

  @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
  private String type;
  @PrimaryKeyColumn(name = "start_time", type = PrimaryKeyType.CLUSTERED, ordinal = 0, ordering = Ordering.DESCENDING)
  private LocalDateTime startTime;
  @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED, ordinal = 1)
  private UUID id;

  public EventKey(final String type, final LocalDateTime startTime, final UUID id) {
    this.type = type;
    this.startTime = startTime;
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }
}
