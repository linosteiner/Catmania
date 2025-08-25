package ch.linosteiner.catmania.services;

import ch.linosteiner.catmania.dto.CatCreateRequest;
import ch.linosteiner.catmania.entities.Behaviour;
import ch.linosteiner.catmania.entities.Breed;
import ch.linosteiner.catmania.entities.Cat;
import ch.linosteiner.catmania.repositories.*;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

@Singleton
public class CatService {
    private final CatRepository catRepository;
    private final BreedRepository breedRepository;
    private final BehaviourRepository behaviourRepository;
    private final CatBehaviourRepository catBehaviourRepository;
    private final CatFriendshipRepository catFriendshipRepository;

    public CatService(CatRepository catRepository, BreedRepository breedRepository, BehaviourRepository behaviourRepository, CatBehaviourRepository catBehaviourRepository, CatFriendshipRepository catFriendshipRepository) {
        this.catRepository = catRepository;
        this.breedRepository = breedRepository;
        this.behaviourRepository = behaviourRepository;
        this.catBehaviourRepository = catBehaviourRepository;
        this.catFriendshipRepository = catFriendshipRepository;
    }

    private static void throwCatNotFoundException(Long id) {
        throw new HttpStatusException(HttpStatus.NOT_FOUND, "Cat %d not found".formatted(id));
    }

    public List<Cat> listCats(@Nullable Long breedPk, @Nullable Long behaviourPk) {
        if (breedPk != null) {
            return catRepository.findByBreedPk(breedPk);
        }
        if (behaviourPk != null) {
            return catRepository.findByBehaviorPk(behaviourPk);
        }
        return catRepository.findAll();
    }

    public Cat getCat(Long pk) {
        return catRepository.findById(pk)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Cat not found"));
    }

    @Transactional
    public Cat createCat(@Valid CatCreateRequest request) {
        Cat cat = new Cat();
        cat.setName(request.name());
        cat.setBirthDate(request.birthDate());

        if (request.fkBreed() != null) {
            Breed breed = breedRepository.findById(request.fkBreed())
                    .orElseThrow(() -> new HttpStatusException(HttpStatus.BAD_REQUEST,
                            "Unknown breed: %d".formatted(request.fkBreed())));
            cat.setBreed(breed);
        }
        cat = catRepository.save(cat);
        if (request.behaviourPks() != null && !request.behaviourPks().isEmpty()) {
            for (Long behaviour : request.behaviourPks()) {
                assertBehaviourExists(behaviour);
                catBehaviourRepository.add(cat.getPk(), behaviour);
            }
        }
        return cat;
    }

    @Transactional
    public Cat update(Long pk, @Valid CatCreateRequest request) {
        Cat cat = getCat(pk);
        if (request.name() != null) {
            cat.setName(request.name());
        }
        if (request.birthDate() != null) {
            cat.setBirthDate(request.birthDate());
        }
        if (request.fkBreed() != null) {
            if (request.fkBreed() == 0) {
                cat.setBreed(null);
            } else {
                Breed breed = breedRepository.findById(request.fkBreed())
                        .orElseThrow(() -> new HttpStatusException(HttpStatus.BAD_REQUEST,
                                "Unknown breed: %d".formatted(request.fkBreed())));
                cat.setBreed(breed);
            }
        }
        cat = catRepository.update(cat);
        if (request.behaviourPks() != null && cat.getBehaviours() != null) {
            for (Long behaviour : request.behaviourPks()) {
                assertBehaviourExists(behaviour);
                catBehaviourRepository.add(cat.getPk(), behaviour);
            }
        }
        return cat;
    }

    @Transactional
    public void deleteCat(Long pk) {
        if (!catRepository.existsById(pk)) {
            throwCatNotFoundException(pk);
        }
        catRepository.deleteById(pk);
    }

    public List<Cat> friendsOf(Long pk) {
        assertCatExists(pk);
        return catRepository.findFriendsOf(pk);
    }

    public void addFriend(@NotNull @Positive Long cat, @NotNull @Positive Long friend) {
        if (cat.equals(friend)) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Cat cannot befriend itself");
        }
        assertCatExists(cat);
        assertCatExists(friend);

        long fkCat = Math.min(cat, friend);
        long fkFriend = Math.min(cat, friend);

        catFriendshipRepository.add(fkCat, fkFriend);
    }

    public void removeFriend(@NotNull @Positive Long cat, @NotNull @Positive Long friend) {
        assertCatExists(cat);
        assertCatExists(friend);
        catFriendshipRepository.deletePair(cat, friend);
    }

    public List<Behaviour> behavioursOf(Long pk) {
        assertCatExists(pk);
        return behaviourRepository.findBehaviorsOf(pk);
    }

    public void addBehaviour(Long pk, @Positive Long behaviourPk) {
        assertCatExists(pk);
        assertBehaviourExists(behaviourPk);
        catBehaviourRepository.add(pk, behaviourPk);
    }

    public void removeBehaviour(Long pk, @Positive Long behaviourPk) {
        assertCatExists(pk);
        assertBehaviourExists(behaviourPk);
        catBehaviourRepository.delete(pk, behaviourPk);
    }

    public void removeAllBehaviours(Long pk) {
        assertCatExists(pk);
        catBehaviourRepository.deleteAllForCat(pk);
    }

    private void assertBehaviourExists(Long id) {
        behaviourRepository.findById(id).orElseThrow(
                () -> new HttpStatusException(HttpStatus.BAD_REQUEST, "Unknown behaviour: " + id)
        );
    }

    private void assertCatExists(Long id) {
        if (!catRepository.existsById(id)) {
            throwCatNotFoundException(id);
        }
    }

}
