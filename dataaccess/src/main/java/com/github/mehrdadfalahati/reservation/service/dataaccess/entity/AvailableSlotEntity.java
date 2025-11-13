package com.github.mehrdadfalahati.reservation.service.dataaccess.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "available_slots")
public class AvailableSlotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time", nullable = false)
    private java.time.Instant startTime;

    @Column(name = "end_time", nullable = false)
    private java.time.Instant endTime;

    @Column(name = "is_reserved", nullable = false)
    private Boolean isReserved;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AvailableSlotEntity that = (AvailableSlotEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
