package com.lankydan.event.repository;

import com.lankydan.event.Event;
import com.lankydan.event.EventKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EventRepository extends CassandraRepository<Event, EventKey> {

  @Query("select avg(value) from event where type = ?0 and start_time > ?1")
  long getAverageValueGreaterThanStartTime(final String type, final LocalDateTime startTime);
}
