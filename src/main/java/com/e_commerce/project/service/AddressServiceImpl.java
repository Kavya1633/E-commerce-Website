package com.e_commerce.project.service;

import com.e_commerce.project.exceptions.APIExceptions;
import com.e_commerce.project.exceptions.ResourceNotFoundException;
import com.e_commerce.project.model.Address;
import com.e_commerce.project.model.User;
import com.e_commerce.project.payload.AddressDTO;
import com.e_commerce.project.repositories.AddressRepository;
import com.e_commerce.project.repositories.UserRepository;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService{
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    UserRepository userRepository;
    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {

        Address address=modelMapper.map(addressDTO,Address.class);
        address.setUser(user);
        if (user.getAddresses() == null) {
            user.setAddresses(new ArrayList<>());
        }
        user.getAddresses().add(address);

        List<Address> addressList=user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);

        Address savedAddress=addressRepository.save(address);
        return modelMapper.map(savedAddress,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddresses() {
        List<Address> addressList=addressRepository.findAll();
        List<AddressDTO> addressDTOList=addressList.stream().map(address -> modelMapper.map(address,AddressDTO.class))
                .collect(Collectors.toList());

        return addressDTOList;
    }

    @Override
    public AddressDTO getAddressbyId(Long addressId) {
        Address address=addressRepository.findById(addressId)
                .orElseThrow(()->new ResourceNotFoundException("address","addressId",addressId));


        return modelMapper.map(address,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddress(User user) {
        List<Address> addressList=user.getAddresses();
        List<AddressDTO> addressDTOList=addressList.stream().map(address -> modelMapper.map(address,AddressDTO.class))
                .collect(Collectors.toList());

        return addressDTOList;
    }

    @Override
    public AddressDTO updateAddress(Long addressId,AddressDTO addressDTO) {
        Address addressFromDatabase = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("address", "addressId", addressId));

        addressFromDatabase.setCity(addressDTO.getCity());
        addressFromDatabase.setPincode(addressDTO.getPincode());
        addressFromDatabase.setCountry(addressDTO.getCountry());
        addressFromDatabase.setHouseNo(addressDTO.getHouseNo());
        addressFromDatabase.setStreet(addressDTO.getStreet());
        addressFromDatabase.setState(addressDTO.getState());

        Address newAddress = addressRepository.save(addressFromDatabase);
        User user = addressFromDatabase.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        user.getAddresses().add(newAddress);

        userRepository.save(user);

        return modelMapper.map(newAddress,AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId) {
        Address existingAddress=addressRepository.findById(addressId)
                .orElseThrow(()->new ResourceNotFoundException("address","addressId",addressId));

        User user=existingAddress.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        userRepository.save(user);
        addressRepository.deleteById(addressId);
        return "Address with addressId "+addressId+" is Successfully deleted!!";
    }


}
