package com.awbd.online_learning.repository;

import com.awbd.online_learning.domain.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Long>{
    Optional<Instructor> findByEmail(String email);
}
