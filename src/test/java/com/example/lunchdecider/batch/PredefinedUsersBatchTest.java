package com.example.lunchdecider.batch;

import com.example.lunchdecider.repo.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class PredefinedUsersBatchTest {

    @Autowired
    UserAccountRepository userRepo;

    @Test
    void predefinedUsersAreLoadedOnStartup() {
        assertThat(userRepo.existsByUsername("alice")).isTrue();
        assertThat(userRepo.existsByUsername("bob")).isTrue();
    }
}
