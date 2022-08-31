package com.food.ordering.system.entity;

import java.util.Objects;

public abstract class BaseEntity<ID> {
    private ID id ;

    public ID getId() {
        return this.id;
    }

    public void setId(ID id) {
        this.id = id;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof BaseEntity)) {
            return false;
        }
        BaseEntity baseEntity = (BaseEntity) o;
        return Objects.equals(id, baseEntity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
 
}
