package com.github.mehrdadfalahati.reservation.service.domain.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for Domain Entities.
 * Entities have a unique identity that runs through time and different representations.
 * They are defined by their identity rather than their attributes.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BaseEntity {
}
