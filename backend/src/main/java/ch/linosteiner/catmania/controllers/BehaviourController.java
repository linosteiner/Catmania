package ch.linosteiner.catmania.controllers;

import ch.linosteiner.catmania.entities.Behaviour;
import ch.linosteiner.catmania.repositories.BehaviourRepository;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.util.List;

@Controller("/api/behaviours")
public class BehaviourController {
    private final BehaviourRepository repo;

    public BehaviourController(BehaviourRepository repo) {
        this.repo = repo;
    }

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Get
    public List<Behaviour> list() {
        return repo.findAll();
    }
}
