package com.e_commerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AddressDTO {
    private Long addressId;
    private String houseNo;
    private String street;
    private String state;
    private String city;
    private String country;
    private String pincode;

}
