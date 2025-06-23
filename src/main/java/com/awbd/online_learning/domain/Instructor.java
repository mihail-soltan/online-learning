package com.awbd.online_learning.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Set;
import java.util.HashSet;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instructor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;

    @Lob
    private String bio;

    @OneToOne(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private InstructorProfile profile;

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<Course> courses = new HashSet<>();

    public void addCourse(Course course) {
        this.courses.add(course);
        course.setInstructor(this);
    }

    public void setInstructorProfile(InstructorProfile profile) {
        if (profile == null) {
            if (this.profile != null) {
                this.profile.setInstructor(null);
            }
        } else {
            profile.setInstructor(this);
        }
        this.profile = profile;
    }
}
