package com.example.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.models.Folder;

public interface FolderRepository extends JpaRepository<Folder, UUID> {
    Folder findByName(String name, String ownerId);
    List<Folder> findByNameContaining(String keyword, String ownerId);
    List<Folder> findAllByOrderByCreationDateAsc(String ownerId);
    List<Folder> findAllByOwnerId(String ownerId);
}
