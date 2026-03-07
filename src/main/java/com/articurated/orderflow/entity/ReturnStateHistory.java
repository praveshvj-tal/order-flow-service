package com.articurated.orderflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "return_state_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnStateHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnState previousState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnState newState;

    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @PrePersist
    void prePersist() {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
}

