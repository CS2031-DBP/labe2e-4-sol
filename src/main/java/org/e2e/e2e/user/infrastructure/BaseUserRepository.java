package org.e2e.e2e.user.infrastructure;

import org.e2e.e2e.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BaseUserRepository<T extends User> extends JpaRepository<T, Long> {
    Optional<T> findByEmail(String email);
}

