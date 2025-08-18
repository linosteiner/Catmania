package ch.linosteiner.catmania.controllers;

import ch.linosteiner.catmania.entities.Behaviour;
import ch.linosteiner.catmania.entities.Breed;
import ch.linosteiner.catmania.entities.Cat;
import ch.linosteiner.catmania.repositories.*;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("/cats")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CatController {
    private final BehaviourRepository behaviourRepository;
    private final BreedRepository breedRepository;
    private final CatRepository catRepository;

    public CatController(
            BehaviourRepository behaviourRepository,
            BreedRepository breedRepository,
            CatRepository catRepository
    ) {
        this.behaviourRepository = behaviourRepository;
        this.breedRepository = breedRepository;
        this.catRepository = catRepository;
    }

    @Get
    public List<Cat> findAll() {
        return catRepository.findAll();
    }

    @Get("/{pk}")
    public HttpResponse<Map<String, Object>> get(Long pk) {
        return catRepository.findById(pk)
                .map(cat -> {
                    List<Behaviour> behaviours = behaviourRepository.findBehaviorsOf(pk);
                    Map<String, Object> map = new HashMap<>();
                    map.put("cat", cat);
                    map.put("breed", cat.getBreed() == null ? null : breedRepository.findById(cat.getPk()).map(Breed::getName).orElse(null));
                    map.put("behaviours", behaviours.stream().map(Behaviour::getName).toList());
                    map.put("friends", catRepository.findFriendsOf(pk).stream().map(Cat::getName).toList());
                    return HttpResponse.ok(map);
                }).orElse(HttpResponse.notFound());
    }
}
