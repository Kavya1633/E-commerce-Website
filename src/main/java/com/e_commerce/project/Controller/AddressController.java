package com.e_commerce.project.Controller;

import com.e_commerce.project.Util.AuthUtil;
import com.e_commerce.project.model.Address;
import com.e_commerce.project.model.User;
import com.e_commerce.project.payload.AddressDTO;
import com.e_commerce.project.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {
    @Autowired
    AuthUtil authUtil;
    @Autowired
    AddressService addressService;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO){

        User user=authUtil.loggedInUser();
        System.out.println("Inside AddressController");
        System.out.println("Auth: " + user);

        AddressDTO savedAddressDTO= addressService.createAddress(addressDTO,user);

        return new ResponseEntity<>(savedAddressDTO, HttpStatus.CREATED);
    }

    @GetMapping("/getAddresses")
    public ResponseEntity<List> getAddresses(){
        List<AddressDTO> addressDTOList=addressService.getAddresses();

        return new ResponseEntity<>(addressDTOList,HttpStatus.OK);
    }
    @GetMapping("/address/{addressId}")
    public ResponseEntity<AddressDTO> getAddressbyId(@PathVariable Long addressId){
       AddressDTO addressDTO=addressService.getAddressbyId(addressId);

        return new ResponseEntity<>(addressDTO,HttpStatus.OK);
    }

    @GetMapping("/users/address")
    public ResponseEntity<List> getUserAddress(){
        User user=authUtil.loggedInUser();
        List<AddressDTO> addressDTOList=addressService.getUserAddress(user);

        return new ResponseEntity<>(addressDTOList,HttpStatus.OK);
    }
    @PutMapping("/address/{addressId}")
        public ResponseEntity<AddressDTO> updateAddress(@Valid @RequestBody AddressDTO addressDTO, @PathVariable Long addressId){
            AddressDTO updatedAddressDTO=addressService.updateAddress(addressId,addressDTO);
            return new ResponseEntity<>(updatedAddressDTO,HttpStatus.OK);
        }
    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId){
        String status=addressService.deleteAddress(addressId);
        return new ResponseEntity<>(status,HttpStatus.OK);
    }
}

