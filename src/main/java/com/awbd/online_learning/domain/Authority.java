package com.awbd.online_learning.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "authorities")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}