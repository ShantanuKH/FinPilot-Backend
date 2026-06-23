package com.shantanu.FinPilot.user.repository;

import com.shantanu.FinPilot.user.entity.Role;
import com.shantanu.FinPilot.user.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository
        extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleType name);

}