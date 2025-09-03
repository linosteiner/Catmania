package ch.linosteiner.catmania.repositories;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface CatFriendshipRepository {

    // Store normalized pair (min, max)
    @Query("""
                INSERT INTO cat_friendship (cat_id, friend_id)
                VALUES (LEAST(:a, :b), GREATEST(:a, :b))
                ON CONFLICT DO NOTHING
            """)
    void add(long a, long b);

    @Query("""
                DELETE FROM cat_friendship
                WHERE cat_id = LEAST(:a, :b) AND friend_id = GREATEST(:a, :b)
            """)
    void deletePair(long a, long b);

    // Friends as Cat rows
    @Query("""
                SELECT c.* FROM cat c
                JOIN cat_friendship f
                  ON (f.cat_id = :id AND c.id = f.friend_id)
                  OR (f.friend_id = :id AND c.id = f.cat_id)
                ORDER BY c.id
            """)
    java.util.List<ch.linosteiner.catmania.entities.Cat> findFriendsOf(long id);
}
