package com.e_commerce.project.repositories;

import com.e_commerce.project.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address,Long>{
}
