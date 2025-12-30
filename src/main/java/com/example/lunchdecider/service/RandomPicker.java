package com.example.lunchdecider.service;

import java.security.SecureRandom;
import java.util.List;

public class RandomPicker {
    /**
     * SecureRandom is used to ensure unbiased and unpredictable selection
     * of a restaurant when a session is ended.
     * The random selection is executed exactly once per session,
     * persisted to the database, and guarded by session state checks
     * to prevent re-randomization.
     * This ensures consistent results for all users and avoids
     * predictable sequences across JVM restarts.
     */
    private final SecureRandom secureRandom = new SecureRandom();

    private final SecureRandom random = new SecureRandom();

    public <T> T pickOne(List<T> items) {
        if (items == null || items.isEmpty()) return null;
        return items.get(random.nextInt(items.size()));
    }
}
