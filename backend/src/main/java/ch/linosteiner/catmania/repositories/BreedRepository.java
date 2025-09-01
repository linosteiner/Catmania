package ch.linosteiner.catmania.repositories;

import ch.linosteiner.catmania.entities.Breed;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface BreedRepository extends CrudRepository<Breed, Long> {
}
