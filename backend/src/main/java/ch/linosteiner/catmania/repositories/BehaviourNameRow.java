package ch.linosteiner.catmania.repositories;

import io.micronaut.core.annotation.Introspected;

@Introspected
public record BehaviourNameRow(Long catId, String name) {
}
