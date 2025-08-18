package ch.linosteiner.catmania.repositories;

import ch.linosteiner.catmania.entities.Behaviour;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface BehaviourRepository extends CrudRepository<Behaviour, Long> {
    Optional<Behaviour> findByName(String name);

    @Query("""
            SELECT b.* FROM behaviour b JOIN cat_behaviour cb ON cb.fk_behaviour = b.pk WHERE cb.fk_cat = :catId
            """)
    List<Behaviour> findBehaviorsOf(Long catId);
}
