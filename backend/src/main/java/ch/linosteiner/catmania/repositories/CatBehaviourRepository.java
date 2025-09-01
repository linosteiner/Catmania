package ch.linosteiner.catmania.repositories;


import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface CatBehaviourRepository {

    @Query("INSERT INTO cat_behaviour (fk_cat, fk_behaviour) VALUES (:catId, :behaviourId) ON CONFLICT DO NOTHING")
    void add(Long catId, Long behaviourId);

    @Query("DELETE FROM cat_behaviour WHERE fk_cat = :catId AND fk_behaviour = :behaviourId")
    void delete(Long catId, Long behaviourId);

    @Query("DELETE FROM cat_behaviour WHERE fk_cat = :catId")
    void deleteAllForCat(Long catId);
}
