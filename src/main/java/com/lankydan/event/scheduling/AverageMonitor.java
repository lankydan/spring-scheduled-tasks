package com.lankydan.event.scheduling;

import com.lankydan.event.Average;
import com.lankydan.event.AverageKey;
import com.lankydan.event.repository.AverageRepository;
import com.lankydan.event.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AverageMonitor {

  private static final Logger LOG = LoggerFactory.getLogger(AverageMonitor.class);
  private final EventRepository eventRepository;
  private final AverageRepository averageRepository;

  public AverageMonitor(
      final EventRepository eventRepository, final AverageRepository averageRepository) {
    this.eventRepository = eventRepository;
    this.averageRepository = averageRepository;
  }

  @Scheduled(cron = "0/20 * * * * ?")
  public void publish() {
    final double average =
        eventRepository.getAverageValueGreaterThanStartTime(
            "An event type", LocalDateTime.now().minusSeconds(20));
    averageRepository.save(
        new Average(new AverageKey("An event type", LocalDateTime.now()), average));
    LOG.info("Average value is {}", average);
  }
}
