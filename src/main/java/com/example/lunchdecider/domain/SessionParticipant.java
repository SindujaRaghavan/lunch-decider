package com.example.lunchdecider.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "session_participant",
        uniqueConstraints = @UniqueConstraint(name = "uq_session_user", columnNames = {"session_id", "user_id"})
)
public class SessionParticipant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private LunchSession session;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    protected SessionParticipant() {}

    public SessionParticipant(LunchSession session, UserAccount user) {
        this.session = session;
        this.user = user;
        this.joinedAt = Instant.now();
    }

    public Long getId() { return id; }
    public LunchSession getSession() { return session; }
    public UserAccount getUser() { return user; }
    public Instant getJoinedAt() { return joinedAt; }
}
