package ch.linosteiner.catmania.repositories;

import ch.linosteiner.catmania.entities.Behaviour;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface BehaviourRepository extends CrudRepository<Behaviour, Long> {

    @Query("""
        SELECT cb.cat_id AS cat_id, b.name AS name
        FROM cat_behaviour cb
        JOIN behaviour b ON b.id = cb.behaviour_id
        WHERE cb.cat_id IN (:catIds)
        ORDER BY cb.cat_id, b.name
    """)
    List<BehaviourNameRow> findNamesByCatIds(List<Long> catIds);

    @Query("""
        SELECT b.*
        FROM behaviour b
        JOIN cat_behaviour cb ON cb.behaviour_id = b.id
        WHERE cb.cat_id = :catId
        ORDER BY b.name
    """)
    List<Behaviour> findByCatId(Long catId);
}
