package com.chuckcha.cloudfilestorage.repository;

import com.chuckcha.cloudfilestorage.entity.Metadata;
import com.chuckcha.cloudfilestorage.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MetadataRepository extends JpaRepository<Metadata, Long> {

    Optional<Metadata> findByPathAndNameAndType(String path, String name, Type type);

    List<Metadata> findAllByNameContains(String name);

    boolean existsByPathAndNameAndType(String path, String name, Type type);

    void deleteByPathAndNameAndType(String path, String name, Type type);

    @Modifying
    @Query("DELETE FROM Metadata m WHERE m.path LIKE CONCAT(:path,'%')")
    void deleteAllByPathStartingWith(String path);

    @Query("SELECT m FROM Metadata m WHERE m.path LIKE CONCAT(:path,'%')")
    List<Metadata> findAllByPathStartsWith(String path);

    @Query("SELECT m FROM Metadata m " +
           "WHERE m.path LIKE CONCAT(:userDirectory,'%') " +
           "AND LOWER(m.name) LIKE LOWER(CONCAT('%', :request, '%'))")
    List<Metadata> search(String userDirectory, String request);

    List<Metadata> findAllByPath(String path);
}
