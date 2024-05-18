package org.e2e.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

@SpringBootTest
@Profile("test")
class E2eApplicationTests {

    @Test
    void contextLoads() {
    }

}
