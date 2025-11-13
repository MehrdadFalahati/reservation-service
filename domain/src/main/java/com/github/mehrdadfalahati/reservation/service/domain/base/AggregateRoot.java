package com.github.mehrdadfalahati.reservation.service.domain.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for Domain-Driven Design Aggregate Roots.
 * Aggregates are clusters of domain objects that can be treated as a single unit.
 * The Aggregate Root is the only entry point for modifications to the aggregate.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AggregateRoot {
}
