package com.lankydan.event.repository;

import com.lankydan.event.Average;
import com.lankydan.event.AverageKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AverageRepository extends CassandraRepository<Average, AverageKey>{
}
