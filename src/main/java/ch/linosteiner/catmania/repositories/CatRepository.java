package ch.linosteiner.catmania.repositories;

import ch.linosteiner.catmania.entities.Cat;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface CatRepository extends CrudRepository<Cat, Long> {

    List<Cat> findByBreedPk(Long breedPk);

    @Query("""
              SELECT c.* FROM cat c
              JOIN cat_behaviour cb ON cb.fk_cat = c.pk
              WHERE cb.fk_behaviour = :behaviorPk
            """)
    List<Cat> findByBehaviorPk(Long behaviorPk);

    @Query("""
              SELECT c.* FROM cat c
              JOIN cat_friendship f ON (f.fk_cat = c.pk AND f.fk_friend = :catPk)
                                   OR (f.fk_friend = c.pk AND f.fk_cat = :catPk)
            """)
    List<Cat> findFriendsOf(Long catPk);
}
