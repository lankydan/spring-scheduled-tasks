package com.lankydan.event;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table
public class Event {

  @PrimaryKey private EventKey key;
  private double value;

  public Event(final EventKey key, final double value) {
    this.key = key;
    this.value = value;
  }

  public EventKey getKey() {
    return key;
  }

  public void setKey(EventKey key) {
    this.key = key;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }
}
