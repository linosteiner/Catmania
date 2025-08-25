package ch.linosteiner.catmania.controllers;

import ch.linosteiner.catmania.dto.CatCreateRequest;
import ch.linosteiner.catmania.dto.CatFriendshipRequest;
import ch.linosteiner.catmania.entities.Behaviour;
import ch.linosteiner.catmania.entities.Cat;
import ch.linosteiner.catmania.services.CatService;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import java.util.List;

@Controller("/cats")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CatController {

    private final CatService catService;

    public CatController(CatService catService) {
        this.catService = catService;
    }

    @Get
    public List<Cat> listCats(
            @Nullable @QueryValue Long breedPk,
            @Nullable @QueryValue Long behaviourPk
    ) {
        return catService.listCats(breedPk, behaviourPk);
    }

    @Get("/{pk}")
    public Cat getCat(@PathVariable @Positive Long pk) {
        return catService.getCat(pk);
    }

    @Post
    @Consumes(MediaType.APPLICATION_JSON)
    public HttpResponse<Cat> createCat(@Valid @Body CatCreateRequest request) {
        Cat created = catService.createCat(request);
        return HttpResponse.created(created);
    }

    @Put("/{pk}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Cat updateCat(@PathVariable Long pk, @Valid @Body CatCreateRequest request) {
        return catService.update(pk, request);
    }

    @Delete("/{pk}")
    public HttpResponse<Void> deleteCat(@PathVariable @Positive Long pk) {
        catService.deleteCat(pk);
        return HttpResponse.noContent();
    }

    @Get("/{pk}/friends")
    public List<Cat> friendsOf(@PathVariable @Positive Long pk) {
        return catService.friendsOf(pk);
    }

    @Post("/friend")
    @Consumes(MediaType.APPLICATION_JSON)
    public HttpResponse<Void> addFriend(@Valid @Body CatFriendshipRequest request) {
        catService.addFriend(request.cat(), request.friend());
        return HttpResponse.accepted();
    }

    @Delete("/friend")
    @Consumes(MediaType.APPLICATION_JSON)
    public HttpResponse<Void> removeFriend(@Valid @Body CatFriendshipRequest request) {
        catService.removeFriend(request.cat(), request.friend());
        return HttpResponse.noContent();
    }

    @Get("/{pk}/behaviours")
    public List<Behaviour> behavioursOf(@PathVariable @Positive Long pk) {
        return catService.behavioursOf(pk);
    }

    @Post("/{pk}/behaviours/{behaviourPk}")
    public HttpResponse<Void> addBehaviour(@PathVariable @Positive Long pk, @PathVariable @Positive Long behaviourPk) {
        catService.addBehaviour(pk, behaviourPk);
        return HttpResponse.accepted();
    }

    @Delete("/{pk}/behaviours/{behaviourPk}")
    public HttpResponse<Void> removeBehaviour(@PathVariable @Positive Long pk, @PathVariable @Positive Long behaviourPk) {
        catService.removeBehaviour(pk, behaviourPk);
        return HttpResponse.noContent();
    }

    @Delete("/{pk}/behaviours/removeAll")
    public HttpResponse<Void> removeAllBehaviours(@PathVariable Long pk) {
        catService.removeAllBehaviours(pk);
        return HttpResponse.noContent();
    }
}
