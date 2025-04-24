package com.e_commerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="address_id")
    private Long addressId;

    @NotBlank
    @Column(name="house_no")
    private String houseNo;

    @NotBlank
    @Column(name="street")
    private String street;

    @NotBlank
    @Column(name="state")
    private String state;

    @NotBlank
    @Column(name="city")
    private String city;

    @NotBlank
    @Column(name="country")
    private String country;

    @NotBlank
    @Column(name="pincode")
    private String pincode;

    public Address( String houseNo, String street, String state, String city, String country,String pincode){
        this.houseNo = houseNo;
        this.street = street;
        this.state = state;
        this.city = city;
        this.country = country;
        this.pincode = pincode;
    }
    @ToString.Exclude
    @ManyToOne
   @JoinColumn(name="user_id")
    private User user;

}
