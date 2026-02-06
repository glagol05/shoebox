package com.example.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.config.SecurityUtils;
import com.example.models.Folder;
import com.example.repositories.FolderRepository;

@Service
public class FolderService {
    
    private final FolderRepository folderRepository;

    public FolderService(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    public List<Folder> getAllFolders() {
        String ownerId = SecurityUtils.currentUserId();
        return folderRepository.findAllByOwnerId(ownerId);
    }

    public List<Folder> searchByKeyWord(String keyword) {
        String ownerId = SecurityUtils.currentUserId();
        return folderRepository.findByNameContaining(keyword, ownerId);
    }

    public List<Folder> getFoldersByCreationDate() {
        String ownerId = SecurityUtils.currentUserId();
        return folderRepository.findAllByOrderByCreationDateAsc(ownerId);
    }

    public Folder getFolderByName(String folderName) {
        String ownerId = SecurityUtils.currentUserId();
        return folderRepository.findByName(folderName, ownerId);
    }

    public Folder createFolder(String name) {
        String ownerId = SecurityUtils.currentUserId();
        return folderRepository.save(new Folder(name, ownerId));
    }
}