package org.e2e.e2e.driver.infrastructure;

import jakarta.transaction.Transactional;
import org.e2e.e2e.driver.domain.Category;
import org.e2e.e2e.driver.domain.Driver;
import org.e2e.e2e.user.infrastructure.BaseUserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Transactional
@Repository
public interface DriverRepository extends BaseUserRepository<Driver> {
    List<Driver> findAllByCategory(Category category);
}
