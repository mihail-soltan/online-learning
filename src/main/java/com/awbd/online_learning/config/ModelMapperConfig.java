package com.awbd.online_learning.config;


import com.awbd.online_learning.domain.Course;
import com.awbd.online_learning.dtos.CourseDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.typeMap(Course.class, CourseDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getInstructor().getId(), CourseDTO::setInstructorId);
        });
        return modelMapper;
    }
}
