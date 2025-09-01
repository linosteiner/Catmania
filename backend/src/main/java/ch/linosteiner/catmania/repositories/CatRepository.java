package ch.linosteiner.catmania.repositories;

import ch.linosteiner.catmania.dto.CatItem;
import ch.linosteiner.catmania.entities.Cat;
import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Join.Type;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;


@JdbcRepository(dialect = Dialect.POSTGRES)
public interface CatRepository extends CrudRepository<Cat, Long> {

    @Join(value = "breed", type = Type.LEFT)
    List<Cat> findAll();

    @Query("""
            SELECT c.pk AS pk,
                   c.name AS name,
                   c.birth_date,
                   b.name as breed_name
            FROM cat c
            LEFT JOIN breed b ON b.pk = c.fk_breed
            """)
    List<CatItem> findAllWithBreed();

    @Join(value = "breed", type = Type.LEFT)
    List<Cat> findByBreedPk(Long breedPk);

    @Query("""
            SELECT
              c.*,
              b.pk   AS breed_pk,
              b.name AS breed_name
            FROM cat c
            LEFT JOIN breed b       ON b.pk = c.fk_breed
            JOIN cat_behaviour cb   ON cb.fk_cat = c.pk
            WHERE cb.fk_behaviour = :behaviourPk
            """)
    List<Cat> findByBehaviourPk(Long behaviourPk);

    @Query("""
            SELECT
              c.*,
              b.pk   AS breed_pk,
              b.name AS breed_name
            FROM cat c
            JOIN cat_behaviour cb ON cb.fk_cat = c.pk
            JOIN breed b          ON b.pk = c.fk_breed
            WHERE b.pk = :breedPk
              AND cb.fk_behaviour = :behaviourPk
            """)
    List<Cat> findByBreedPkAndBehaviourPk(Long breedPk, Long behaviourPk);

    @Query("""
            SELECT
              c.*,
              b.pk   AS breed_pk,
              b.name AS breed_name
            FROM cat c
            LEFT JOIN breed b ON b.pk = c.fk_breed
            JOIN cat_friendship f ON (f.fk_cat = c.pk AND f.fk_friend = :catPk)
                                 OR (f.fk_friend = c.pk AND f.fk_cat = :catPk)
            """)
    List<Cat> findFriendsOf(Long catPk);
}
