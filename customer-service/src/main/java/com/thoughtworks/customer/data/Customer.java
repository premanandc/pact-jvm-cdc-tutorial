package com.thoughtworks.customer.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("customers")
@Builder
public class Customer implements Persistable<Long> {

    @Transient
    private transient boolean isNew;
    @Id
    private Long id;

    private String firstName;

    private String lastName;

    public Customer(String firstName, String lastName) {
        this(null, firstName, lastName);
    }

    public Customer(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isNew = true;
    }

    @Override
    @JsonIgnore
    public boolean isNew() {
        return isNew;
    }
}
