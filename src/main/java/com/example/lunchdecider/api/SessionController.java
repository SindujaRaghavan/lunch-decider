package com.example.lunchdecider.api;

import com.example.lunchdecider.api.dto.*;
import com.example.lunchdecider.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService service;

    public SessionController(SessionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SessionResponse> create(@RequestBody @Valid CreateSessionRequest req) {
        var session = service.createSession(req.createdByUsername());
        return ResponseEntity.ok(toResponse(service.getSession(session.getCode())));
    }

    @PostMapping("/{code}/join")
    public ResponseEntity<Void> join(@PathVariable("code") String code, @RequestBody @Valid JoinSessionRequest req) {
        service.joinSession(code, req.username());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{code}/restaurants")
    public ResponseEntity<Void> submit(@PathVariable("code") String code, @RequestBody @Valid SubmitRestaurantRequest req) {
        service.submitRestaurant(code, req.username(), req.restaurantName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{code}/end")
    public ResponseEntity<SessionResponse> end(@PathVariable("code") String code, @RequestParam("by") String endedBy) {
        service.endSession(code, endedBy);
        return ResponseEntity.ok(toResponse(service.getSession(code)));
    }

    @GetMapping("/{code}")
    public ResponseEntity<SessionResponse> get(@PathVariable("code") String code) {
        return ResponseEntity.ok(toResponse(service.getSession(code)));
    }

    private SessionResponse toResponse(SessionService.SessionView view) {
        var s = view.session();
        return new SessionResponse(
                s.getCode(),
                s.getStatus().name(),
                s.getCreatedBy().getUsername(),
                view.participants().stream().map(p -> p.getUser().getUsername()).distinct().toList(),
                view.submissions().stream().map(r -> r.getRestaurantName()).distinct().collect(Collectors.toList()),
                s.getPickedRestaurant()
        );
    }
}
