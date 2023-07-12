package com.sunlifemalaysia.app4share.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunlifemalaysia.app4share.model.auth.UserAccount;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    @EntityGraph(attributePaths = { "roles" })
    public Optional<UserAccount> findByUsername(String username);
}