package com.example.lunchdecider.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "restaurant_submission",
        uniqueConstraints = @UniqueConstraint(name = "uq_session_restaurant", columnNames = {"session_id", "restaurant_name"})
)
public class RestaurantSubmission {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private LunchSession session;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Column(name = "restaurant_name", nullable = false, length = 200)
    private String restaurantName;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected RestaurantSubmission() {}

    public RestaurantSubmission(LunchSession session, UserAccount user, String restaurantName) {
        this.session = session;
        this.user = user;
        this.restaurantName = restaurantName;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public LunchSession getSession() { return session; }
    public UserAccount getUser() { return user; }
    public String getRestaurantName() { return restaurantName; }
    public Instant getCreatedAt() { return createdAt; }
}
