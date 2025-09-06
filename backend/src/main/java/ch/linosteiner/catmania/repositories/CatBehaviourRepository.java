package ch.linosteiner.catmania.repositories;

import ch.linosteiner.catmania.entities.CatBehaviour;
import ch.linosteiner.catmania.entities.CatBehaviourId;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface CatBehaviourRepository extends CrudRepository<CatBehaviour, CatBehaviourId> {

    @Query("""
                INSERT INTO cat_behaviour (cat_id, behaviour_id)
                VALUES (:catId, :behaviourId)
                ON CONFLICT DO NOTHING
            """)
    void add(Long catId, Long behaviourId);

    @Query("""
                SELECT EXISTS (
                    SELECT 1 FROM cat_behaviour
                    WHERE cat_id = :catId AND behaviour_id = :behaviourId
                )
            """)
    boolean existsByCatIdAndBehaviourId(Long catId, Long behaviourId);

    @Query("DELETE FROM cat_behaviour WHERE cat_id = :catId AND behaviour_id = :behaviourId")
    void deleteByCatIdAndBehaviourId(Long catId, Long behaviourId);

    @Query("SELECT * FROM cat_behaviour WHERE cat_id = :catId")
    List<CatBehaviour> findByCatId(Long catId);

    @Query("SELECT * FROM cat_behaviour WHERE behaviour_id = :behaviourId")
    List<CatBehaviour> findByBehaviourId(Long behaviourId);

    @Query("DELETE FROM cat_behaviour WHERE cat_id = :catId")
    void deleteByCatId(Long catId);

    @Query("DELETE FROM cat_behaviour WHERE cat_id = :catId")
    void deleteAllByCatId(Long catId);
}
