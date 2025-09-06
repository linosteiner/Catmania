package ch.linosteiner.catmania.controllers;

import ch.linosteiner.catmania.entities.Breed;
import ch.linosteiner.catmania.repositories.BreedRepository;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.util.List;

@Controller("/api/breeds")
public class BreedController {
    private final BreedRepository repo;
    public BreedController(BreedRepository repo) { this.repo = repo; }

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Get
    public List<Breed> list() { return repo.findAll(); }
}
