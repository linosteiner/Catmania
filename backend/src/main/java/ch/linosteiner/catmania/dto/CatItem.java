package ch.linosteiner.catmania.dto;


import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDate;

@Serdeable
public record CatItem(
        Long pk,
        String name,
        LocalDate birthDate,
        String breedName
) {}
