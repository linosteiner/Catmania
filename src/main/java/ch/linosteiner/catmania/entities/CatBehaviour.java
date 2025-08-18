package ch.linosteiner.catmania.entities;

import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;

@MappedEntity("cat_behaviour")
public class CatBehaviour {
    @MappedProperty("fk_cat") Long catPk;
    @MappedProperty("fk_behaviour") Long behaviourPk;
}
