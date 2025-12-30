package com.example.lunchdecider.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "lunch_session")
public class LunchSession {

    public enum Status { ACTIVE, ENDED }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 16, unique = true)
    private String code;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private UserAccount createdBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;

    @Column(name = "picked_restaurant", length = 200)
    private String pickedRestaurant;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Version
    private long version;

    protected LunchSession() {}

    public LunchSession(String code, UserAccount createdBy) {
        this.code = code;
        this.createdBy = createdBy;
        this.status = Status.ACTIVE;
        this.createdAt = Instant.now();
    }

    public void endWithPickedRestaurant(String picked) {
        this.status = Status.ENDED;
        this.pickedRestaurant = picked;
        this.endedAt = Instant.now();
    }

    public boolean isActive() { return this.status == Status.ACTIVE; }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public UserAccount getCreatedBy() { return createdBy; }
    public Status getStatus() { return status; }
    public String getPickedRestaurant() { return pickedRestaurant; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getEndedAt() { return endedAt; }
}
