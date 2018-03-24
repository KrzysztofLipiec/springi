package com.project.security.repository;

import com.project.security.model.AuthorityName;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.security.model.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Authority findByName(AuthorityName name);
}
