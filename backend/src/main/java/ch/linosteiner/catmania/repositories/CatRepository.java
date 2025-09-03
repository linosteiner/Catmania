package ch.linosteiner.catmania.repositories;

import ch.linosteiner.catmania.entities.Cat;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


@JdbcRepository(dialect = Dialect.POSTGRES)
public interface CatRepository extends CrudRepository<Cat, Long> {

    @Join("breed")
    @Join("behaviours")
    List<Cat> findAllByBehavioursId(Long behaviourId);

    @Join("breed")
    @Join("behaviours")
    List<Cat> findAllByBreedId(Long breedId);

    @Join("breed")
    @Join("behaviours")
    List<Cat> findDistinctByBreedIdAndBehavioursId(Long breedId, Long behaviourId);

    @Join("breed")
    @Join("behaviours")
    List<Cat> findAll();

    @Join("breed")
    @Join("behaviours")
    Optional<Cat> findById(Long id);
}
