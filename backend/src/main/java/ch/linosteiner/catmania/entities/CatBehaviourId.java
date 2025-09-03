package ch.linosteiner.catmania.entities;


import io.micronaut.data.annotation.Embeddable;
import io.micronaut.data.annotation.MappedProperty;

import java.io.Serializable;

@Embeddable
public record CatBehaviourId(
        @MappedProperty("cat_id") Long catId,
        @MappedProperty("behaviour_id") Long behaviourId
) implements Serializable {
}
