package ch.linosteiner.catmania.dto;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;


@Serdeable
public record CatCreateRequest(
        @NotBlank String name,
        @PastOrPresent LocalDate birthDate,
        @Positive Long fkBreed,
        List<@Positive Long> behaviourPks
) {
}
