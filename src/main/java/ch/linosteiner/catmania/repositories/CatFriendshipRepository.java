package ch.linosteiner.catmania.repositories;

import ch.linosteiner.catmania.entities.Cat;
import ch.linosteiner.catmania.entities.CatFriendship;
import ch.linosteiner.catmania.entities.CatFriendshipId;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface CatFriendshipRepository extends CrudRepository<CatFriendship, CatFriendshipId> {

    @Query("""
              DELETE FROM cat_friendship
              WHERE (fk_cat = :cat AND fk_friend = :friend)
                 OR (fk_cat = :friend AND fk_friend = :cat)
            """)
    long deletePair(Long cat, Long friend);

    @Query("""
              SELECT c.* FROM cat c
              JOIN cat_friendship f ON (f.fk_cat = c.pk AND f.fk_friend = :catPk)
                                   OR (f.fk_friend = c.pk AND f.fk_cat = :catPk)
            """)
    List<Cat> findFriends(@Nullable Long catPk);
}
