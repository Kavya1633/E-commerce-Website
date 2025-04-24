package com.e_commerce.project.service;

import com.e_commerce.project.model.User;
import com.e_commerce.project.payload.AddressDTO;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAddresses();

    AddressDTO getAddressbyId(Long addressId);

    List<AddressDTO> getUserAddress(User user);

    AddressDTO updateAddress(Long addressId,AddressDTO addressDTO);

    String deleteAddress(Long addressId);
}
