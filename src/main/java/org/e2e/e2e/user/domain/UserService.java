package org.e2e.e2e.user.domain;

import org.e2e.e2e.driver.infrastructure.DriverRepository;
import org.e2e.e2e.passenger.exceptions.UnauthorizeOperationException;
import org.e2e.e2e.passenger.infrastructure.PassengerRepository;
import org.e2e.e2e.user.infrastructure.BaseUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private BaseUserRepository<User> userRepository;
    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private DriverRepository driverRepository;

    public User findByEmail(String username, String role) {
        User user;
        if (role.equals("ROLE_DRIVER"))
            user = driverRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        else
            user = passengerRepository.findByEmail(username).orElseThrow(() -> new UnauthorizeOperationException("User has no permission"));

        return user;
    }
    @Bean(name = "UserDetailsService")
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository
                    .findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return (UserDetails) user;
        };
    }
}
