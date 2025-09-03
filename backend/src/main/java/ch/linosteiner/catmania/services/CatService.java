package ch.linosteiner.catmania.services;

import ch.linosteiner.catmania.dto.CatCreateRequest;
import ch.linosteiner.catmania.dto.CatUpdateRequest;
import ch.linosteiner.catmania.entities.Behaviour;
import ch.linosteiner.catmania.entities.Breed;
import ch.linosteiner.catmania.entities.Cat;
import ch.linosteiner.catmania.entities.CatBehaviour;
import ch.linosteiner.catmania.repositories.*;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class CatService {
    private final CatRepository catRepository;
    private final BreedRepository breedRepository;
    private final BehaviourRepository behaviourRepository;
    private final CatBehaviourRepository catBehaviourRepository;
    private final CatFriendshipRepository catFriendshipRepository;

    public CatService(CatRepository catRepository,
                      BreedRepository breedRepository,
                      BehaviourRepository behaviourRepository,
                      CatBehaviourRepository catBehaviourRepository,
                      CatFriendshipRepository catFriendshipRepository
    ) {
        this.catRepository = catRepository;
        this.breedRepository = breedRepository;
        this.behaviourRepository = behaviourRepository;
        this.catBehaviourRepository = catBehaviourRepository;
        this.catFriendshipRepository = catFriendshipRepository;
    }

    private static void throwCatNotFoundException(Long id) {
        throw new HttpStatusException(HttpStatus.NOT_FOUND, "Cat %d not found".formatted(id));
    }

    public List<Cat> listCats(@Nullable Long breedId, @Nullable Long behaviourId) {
        if (breedId != null && behaviourId != null) {
            return catRepository.findDistinctByBreedIdAndBehavioursId(breedId, behaviourId);
        }
        if (breedId != null) {
            return catRepository.findAllByBreedId(breedId);
        }
        if (behaviourId != null) {
            return catRepository.findAllByBehavioursId(behaviourId);
        }
        return catRepository.findAll();
    }

    public Cat getCat(Long id) {
        return catRepository.findById(id)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Cat not found"));
    }

    @Transactional
    public Cat createCat(@Valid CatCreateRequest request) {
        Cat cat = new Cat();
        cat.setName(request.name());
        cat.setBirthDate(request.birthDate());

        if (request.id_breed() != null) {
            Breed breed = breedRepository.findById(request.id_breed())
                    .orElseThrow(() -> new HttpStatusException(HttpStatus.BAD_REQUEST,
                            "Unknown breed: %d".formatted(request.id_breed())));
            cat.setBreed(breed);
        }
        cat = catRepository.save(cat);
        if (request.behaviourIds() != null && !request.behaviourIds().isEmpty()) {
            for (Long behaviour : request.behaviourIds()) {
                assertBehaviourExists(behaviour);
                catBehaviourRepository.save(new CatBehaviour(cat.getId(), behaviour));
            }
        }
        return cat;
    }

    @Transactional
    public Cat update(Long id, @Valid CatUpdateRequest request) {
        Cat cat = getCat(id);

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
                        .orElseThrow(() -> new HttpStatusException(
                                HttpStatus.BAD_REQUEST, "Unknown breed: %d".formatted(request.fkBreed())));
                cat.setBreed(breed);
            }
        }

        cat = catRepository.update(cat);

        if (request.behaviourIds() != null) {
            for (Long behaviourId : request.behaviourIds()) {
                assertBehaviourExists(behaviourId);
                catBehaviourRepository.add(cat.getId(), behaviourId);
            }
        }

        return cat;
    }

    @Transactional
    public void deleteCat(Long id) {
        if (!catRepository.existsById(id)) {
            throwCatNotFoundException(id);
        }
        catRepository.deleteById(id);
    }

    public List<Cat> friendsOf(Long id) {
        assertCatExists(id);
        return catFriendshipRepository.findFriendsOf(id);
    }

    public void addFriend(long aId, long bId) {
        assertCatExists(aId);
        assertCatExists(bId);
        if (aId == bId) return; // ignore self
        catFriendshipRepository.add(aId, bId);
    }

    public void removeFriend(long aId, long bId) {
        catFriendshipRepository.deletePair(aId, bId);
    }

    public List<Behaviour> behavioursOf(Long id) {
        assertCatExists(id);
        return behaviourRepository.findByCatId(id);
    }

    public void addBehaviour(Long id, @Positive Long behaviourId) {
        assertCatExists(id);
        assertBehaviourExists(behaviourId);
        catBehaviourRepository.save(new CatBehaviour(id, behaviourId));
    }

    public void removeBehaviour(Long id, @Positive Long behaviourId) {
        assertCatExists(id);
        assertBehaviourExists(behaviourId);
        catBehaviourRepository.delete(new CatBehaviour(id, behaviourId));
    }

    public void removeAllBehaviours(Long id) {
        assertCatExists(id);
        catBehaviourRepository.deleteByCatId(id);
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

    public Map<Long, List<String>> behaviourNamesForCats(Collection<Long> catIds) {
        if (catIds == null || catIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<BehaviourNameRow> rows = behaviourRepository.findNamesByCatIds(new ArrayList<>(catIds));

        return rows.stream().collect(
                Collectors.groupingBy(
                        BehaviourNameRow::catId,
                        LinkedHashMap::new,
                        Collectors.mapping(BehaviourNameRow::name, Collectors.toList())
                )
        );
    }
}
