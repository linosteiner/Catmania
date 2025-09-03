package ch.linosteiner.catmania.dto;


import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDate;
import java.util.List;

@Serdeable
public record CatItem(
        Long id,
        String name,
        LocalDate birthDate,
        String breedName,
        List<String> behaviours
) {
}
