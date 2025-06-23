package com.awbd.online_learning.repository;

import com.awbd.online_learning.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long>{

    Optional<Authority> findByName(String name);
}
