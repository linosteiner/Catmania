package ch.linosteiner.catmania.dto;

import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDate;
import java.util.List;

@Serdeable
public record CatItemWithBehaviours(
        String name,
        LocalDate localDate,
        String breedName,
        List<String> behaviours
) {
}
