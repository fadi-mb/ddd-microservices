package com.food.ordering.system.order.service.valueobject;

import java.util.Objects;
import java.util.UUID;
 

public class StreetAddress   {
    private final UUID id;
    private final String street;
    private final String postalCode;
    private final String city;

    public UUID getId() {
        return this.id;
    }


    public String getStreet() {
        return this.street;
    }


    public String getPostalCode() {
        return this.postalCode;
    }


    public String getCity() {
        return this.city;
    }


    public StreetAddress(UUID id, String street, String postalCode, String city) {
        this.id = id;
        this.street = street;
        this.postalCode = postalCode;
        this.city = city;
    }

 
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof StreetAddress)) {
            return false;
        }
        StreetAddress streetAddress = (StreetAddress) o;
        return Objects.equals(street, streetAddress.street) && Objects.equals(postalCode, streetAddress.postalCode) && Objects.equals(city, streetAddress.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, postalCode, city);
    }
}
