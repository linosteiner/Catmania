package ch.linosteiner.catmania.controllers;

import ch.linosteiner.catmania.dto.CatCreateRequest;
import ch.linosteiner.catmania.dto.CatItem;
import ch.linosteiner.catmania.dto.CatUpdateRequest;
import ch.linosteiner.catmania.entities.Behaviour;
import ch.linosteiner.catmania.entities.Cat;
import ch.linosteiner.catmania.services.CatService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Validated
@Controller("/api/cats")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CatController {

    private final CatService catService;

    public CatController(CatService catService) {
        this.catService = catService;
    }

    private static CatItem toItem(Cat cat, List<String> behaviourNames) {
        String breedName = cat.getBreed() != null ? cat.getBreed().getName() : null;
        return new CatItem(cat.getId(), cat.getName(), cat.getBirthDate(), breedName, behaviourNames);
    }

    private List<CatItem> toItems(List<Cat> cats) {
        List<Long> ids = cats.stream().map(Cat::getId).toList();
        Map<Long, List<String>> behavioursByCat = catService.behaviourNamesForCats(ids); // new helper

        return cats.stream()
                .map(c -> toItem(c, behavioursByCat.getOrDefault(c.getId(), List.of())))
                .toList();
    }

    @Get
    public List<CatItem> listCats(@QueryValue Optional<@Positive Long> breedId,
                                  @QueryValue Optional<@Positive Long> behaviourId) {
        return toItems(catService.listCats(breedId.orElse(null), behaviourId.orElse(null)));
    }



    @Get("/{id}")
    public CatItem getCat(@PathVariable @Positive Long id) {
        Cat cat = catService.getCat(id);
        Map<Long, List<String>> map = catService.behaviourNamesForCats(List.of(cat.getId()));
        return toItem(cat, map.getOrDefault(cat.getId(), List.of()));
    }

    @Post
    public HttpResponse<CatItem> createCat(@Valid @Body CatCreateRequest request) {
        Cat cat = catService.createCat(request);

        Map<Long, List<String>> map = catService.behaviourNamesForCats(java.util.List.of(cat.getId()));
        List<String> behaviours = map.getOrDefault(cat.getId(), java.util.List.of());

        URI location = io.micronaut.http.uri.UriBuilder.of("/api/cats/{id}")
                .expand(java.util.Collections.singletonMap("id", cat.getId()));
        return io.micronaut.http.HttpResponse.created(toItem(cat, behaviours), location);
    }


    @Patch("/{id}")
    public CatItem updateCat(@PathVariable @jakarta.validation.constraints.Positive Long id,
                             @Valid @Body CatUpdateRequest request) {
        Cat cat = catService.update(id, request);

        Map<Long, List<String>> map = catService.behaviourNamesForCats(java.util.List.of(cat.getId()));
        List<String> behaviours = map.getOrDefault(cat.getId(), java.util.List.of());

        return toItem(cat, behaviours);
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    public void deleteCat(@PathVariable @Positive Long id) {
        catService.deleteCat(id);
    }

    @Get("/{id}/friends")
    public List<CatItem> friendsOf(@PathVariable @Positive Long id) {
        return toItems(catService.friendsOf(id));
    }

    @Put("/{id}/friends/{friendId}")
    @Status(HttpStatus.NO_CONTENT)
    public void addFriend(@PathVariable @Positive Long id,
                          @PathVariable @Positive Long friendId) {
        catService.addFriend(id, friendId);
    }

    @Delete("/{id}/friends/{friendId}")
    @Status(HttpStatus.NO_CONTENT)
    public void removeFriend(@PathVariable @Positive Long id,
                             @PathVariable @Positive Long friendId) {
        catService.removeFriend(id, friendId);
    }

    @Get("/{id}/behaviours")
    public List<Behaviour> behavioursOf(@PathVariable @Positive Long id) {
        return catService.behavioursOf(id);
    }

    @Put("/{id}/behaviours/{behaviourId}")
    @Status(HttpStatus.NO_CONTENT)
    public void addBehaviour(@PathVariable @Positive Long id,
                             @PathVariable @Positive Long behaviourId) {
        catService.addBehaviour(id, behaviourId);
    }

    @Delete("/{id}/behaviours/{behaviourId}")
    @Status(HttpStatus.NO_CONTENT)
    public void removeBehaviour(@PathVariable @Positive Long id,
                                @PathVariable @Positive Long behaviourId) {
        catService.removeBehaviour(id, behaviourId);
    }

    @Delete("/{id}/behaviours")
    @Status(HttpStatus.NO_CONTENT)
    public void removeAllBehaviours(@PathVariable @Positive Long id) {
        catService.removeAllBehaviours(id);
    }
}
