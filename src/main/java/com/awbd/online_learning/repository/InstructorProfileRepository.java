package com.awbd.online_learning.repository;

import com.awbd.online_learning.domain.InstructorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructorProfileRepository extends JpaRepository<InstructorProfile, Long>{
}
