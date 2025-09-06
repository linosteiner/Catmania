package ch.linosteiner.catmania.controllers;

import ch.linosteiner.catmania.entities.Breed;
import ch.linosteiner.catmania.repositories.BreedRepository;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

import java.util.List;

@Controller("/api/breeds")
public class BreedController {
    private final BreedRepository repo;
    public BreedController(BreedRepository repo) { this.repo = repo; }

    @Get
    public List<Breed> list() { return repo.findAll(); }
}
