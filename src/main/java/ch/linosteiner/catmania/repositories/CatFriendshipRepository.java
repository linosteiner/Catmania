package ch.linosteiner.catmania.repositories;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface CatFriendshipRepository {
    @Query("INSERT INTO cat_friendship (fk_cat, fk_friend) VALUES (:fkCat, :fkFriend) ON CONFLICT DO NOTHING")
    void add(long fkCat, long fkFriend);

    @Query("DELETE FROM cat_friendship WHERE (fk_cat = :a AND fk_friend = :b) OR (fk_cat = :b AND fk_friend = :a)")
    void deletePair(long a, long b);

}
