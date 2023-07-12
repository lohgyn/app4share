package com.sunlifemalaysia.app4share.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunlifemalaysia.app4share.model.auth.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    public Optional<Role> findByName(String name);

}