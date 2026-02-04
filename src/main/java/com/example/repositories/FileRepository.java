package com.example.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.models.File;
import com.example.models.Folder;


public interface FileRepository extends JpaRepository<File, UUID> {
    List<File> findByFolder(Folder folder);
    List<File> findAllByName(String name);
    List<File> findByNameContaining(String keyword);
    Optional<File> findByName(String name);
}
