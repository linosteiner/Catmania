package ch.linosteiner.catmania.entities;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.EmbeddedId;
import io.micronaut.data.annotation.MappedEntity;

@MappedEntity("cat_behaviour")
@Introspected
public class CatBehaviour {
    @EmbeddedId
    private CatBehaviourId id;

    public CatBehaviour(Long catId, Long behaviourId) {
        this.id = new CatBehaviourId(catId, behaviourId);
    }

    public CatBehaviourId getId() {
        return id;
    }

    public void setId(CatBehaviourId id) {
        this.id = id;
    }
}
