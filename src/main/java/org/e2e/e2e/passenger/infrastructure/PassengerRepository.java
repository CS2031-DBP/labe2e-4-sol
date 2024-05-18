package org.e2e.e2e.passenger.infrastructure;

import jakarta.transaction.Transactional;
import org.e2e.e2e.passenger.domain.Passenger;
import org.e2e.e2e.user.infrastructure.BaseUserRepository;
import org.springframework.stereotype.Repository;


@Transactional
@Repository
public interface PassengerRepository extends BaseUserRepository<Passenger> {
}
