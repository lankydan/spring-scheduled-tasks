package com.lankydan.event;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table
public class Average {

  @PrimaryKey
  private AverageKey key;
  private double value;

  public Average(AverageKey key, double value) {
    this.key = key;
    this.value = value;
  }

  public AverageKey getKey() {
    return key;
  }

  public void setKey(AverageKey key) {
    this.key = key;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }
}
