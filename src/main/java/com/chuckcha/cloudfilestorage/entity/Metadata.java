package com.chuckcha.cloudfilestorage.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "metadata")
public class Metadata extends AuditingEntity<Long>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String path;

    @Column(nullable = false)
    String name;

    Long size;

    @Enumerated(EnumType.STRING)
    Type type;
}
