package com.example.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.models.Folder;
import com.example.repositories.FolderRepository;

@Service
public class FolderService {
    
    private final FolderRepository folderRepository;

    public FolderService(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    public List<Folder> getAllFolders() {
        return folderRepository.findAll();
    }

    public List<Folder> getFoldersByCreationDate() {
        return folderRepository.findAllByOrderByCreationDateAsc();
    }

    public Folder getFolderByName(String folderName) {
        return folderRepository.findByName(folderName);
    }

    public Folder createFolder(String name) {
        Folder folder = new Folder(name);
        return folderRepository.save(folder);
    }

    public Folder saveFolder(Folder folder) {
        return folderRepository.save(folder);
    }

    public void deleteFolder(String folderName) {
        Folder folder = getFolderByName(folderName);
        folderRepository.delete(folder);
    }
}