package com.awbd.online_learning.repository;

import com.awbd.online_learning.domain.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    List<Module> findByCourseId(Long courseId);
}
