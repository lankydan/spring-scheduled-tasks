package com.lankydan.event.scheduling;

import com.lankydan.event.Event;
import com.lankydan.event.EventKey;
import com.lankydan.event.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class EventCreator {

  private static final Logger LOG = LoggerFactory.getLogger(EventCreator.class);

  private final EventRepository eventRepository;

  public EventCreator(final EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  @Scheduled(fixedRate = 1000)
  public void create() {
    final LocalDateTime start = LocalDateTime.now();
    eventRepository.save(
        new Event(new EventKey("An event type", start, UUID.randomUUID()), Math.random() * 1000));
    LOG.debug("Event created!");
  }
}
