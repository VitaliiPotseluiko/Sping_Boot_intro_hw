package com.springboot.intro.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsExclude;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import java.util.Set;

@Getter
@Setter
@ToString
@SQLDelete(sql = "UPDATE shopping_carts SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
@Entity
@Table(name = "shopping_carts")
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @HashCodeExclude
    @EqualsExclude
    @ToStringExclude
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
    @HashCodeExclude
    @EqualsExclude
    @ToString.Exclude
    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> cartItems;
    @Column(nullable = false)
    private boolean isDeleted;
}
