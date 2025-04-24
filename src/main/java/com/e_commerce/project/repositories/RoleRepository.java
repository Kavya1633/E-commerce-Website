package com.e_commerce.project.repositories;

import com.e_commerce.project.model.AppRole;
import com.e_commerce.project.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRole appRole);

}
