package com.example.lunchdecider.service;

import com.example.lunchdecider.domain.*;
import com.example.lunchdecider.repo.*;
import com.example.lunchdecider.support.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SessionService {

    private final UserAccountRepository userRepo;
    private final LunchSessionRepository sessionRepo;
    private final SessionParticipantRepository participantRepo;
    private final RestaurantSubmissionRepository submissionRepo;
    private final RandomPicker picker = new RandomPicker();

    public SessionService(UserAccountRepository userRepo,
                          LunchSessionRepository sessionRepo,
                          SessionParticipantRepository participantRepo,
                          RestaurantSubmissionRepository submissionRepo) {
        this.userRepo = userRepo;
        this.sessionRepo = sessionRepo;
        this.participantRepo = participantRepo;
        this.submissionRepo = submissionRepo;
    }

    @Transactional
    public LunchSession createSession(String createdByUsername) {
        UserAccount creator = userRepo.findByUsername(createdByUsername)
                .orElseThrow(() -> new NotFoundException("User not allowed / not found: " + createdByUsername));

        String code = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        LunchSession session = sessionRepo.save(new LunchSession(code, creator));

        // creator auto-joins the session
        participantRepo.save(new SessionParticipant(session, creator));
        return session;
    }

    @Transactional
    public void joinSession(String sessionCode, String username) {
        LunchSession session = sessionRepo.findByCode(sessionCode)
                .orElseThrow(() -> new NotFoundException("Session not found: " + sessionCode));

        if (!session.isActive()) {
            throw new IllegalStateException("Session already ended: " + sessionCode);
        }

        UserAccount user = userRepo.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not allowed / not found: " + username));

        try {
            if (!participantRepo.existsBySession_IdAndUser_Id(session.getId(), user.getId())) {
                participantRepo.save(new SessionParticipant(session, user));
            }
        } catch (DataIntegrityViolationException e) {
            // safe for concurrent joins; unique constraint prevents duplicates
        }
    }

    @Transactional
    public void submitRestaurant(String sessionCode, String username, String restaurantName) {
        LunchSession session = sessionRepo.findByCode(sessionCode)
                .orElseThrow(() -> new NotFoundException("Session not found: " + sessionCode));
        if (!session.isActive()) throw new IllegalStateException("Session already ended: " + sessionCode);

        UserAccount user = userRepo.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not allowed / not found: " + username));

        if (!participantRepo.existsBySession_IdAndUser_Id(session.getId(), user.getId())) {
            throw new IllegalStateException("User must join the session before submitting restaurants.");
        }

        // requirement: one user’s submitted location should not break others -> validate/sanitize length
        String safeName = restaurantName == null ? "" : restaurantName.trim();
        if (safeName.isBlank() || safeName.length() > 200) {
            throw new IllegalArgumentException("Invalid restaurant name.");
        }

        try {
            submissionRepo.save(new RestaurantSubmission(session, user, safeName));
        } catch (DataIntegrityViolationException e) {
            // prevent duplicate restaurant in a session (uq_session_restaurant)
            throw new IllegalStateException("Restaurant already submitted in this session.");
        }
    }

    @Transactional
    public void endSession(String sessionCode, String endedByUsername) {

        LunchSession session = sessionRepo.findByCode(sessionCode)
                .orElseThrow(() -> new NotFoundException("Session not found: " + sessionCode));
        // Prevent re-randomization if already ended
        if (!session.isActive()) {
           return;
        }

        if (!session.getCreatedBy().getUsername().equals(endedByUsername)) {
            throw new IllegalStateException("Only the session creator can end the session.");
        }

        List<RestaurantSubmission> submissions = submissionRepo.findBySessionIdWithUserOrderByCreatedAtAsc(session.getId());
        if (submissions.isEmpty()) {
            session.endWithPickedRestaurant(null);
           sessionRepo.save(session);
           return;
        }
        // Random selection is performed only when the session transitions
        // from ACTIVE to ENDED. Once ended, this logic is never executed again.
        RestaurantSubmission picked = picker.pickOne(submissions);
        session.endWithPickedRestaurant(picked.getRestaurantName());
        sessionRepo.save(session);
    }

    @Transactional
    public SessionView getSession(String sessionCode) {
        LunchSession session = sessionRepo.findByCodeWithCreator(sessionCode)
                .orElseThrow(() -> new NotFoundException("Session not found: " + sessionCode));

        List<SessionParticipant> participants = participantRepo.findBySessionIdWithUser(session.getId());
        List<RestaurantSubmission> submissions = submissionRepo.findBySessionIdWithUserOrderByCreatedAtAsc(session.getId());

        return new SessionView(session, participants, submissions);
    }

    public record SessionView(LunchSession session,
                              List<SessionParticipant> participants,
                              List<RestaurantSubmission> submissions) {}
}
