package com.example.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.models.Folder;
import com.example.models.User;
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

    public List<Folder> getFoldersByKeyword(String keyword) {
        return folderRepository.findByNameContaining(keyword);
    }

    public Folder createFolder(String name, User user) {
        Folder folder = new Folder(name, user);
        return folderRepository.save(folder);
    }

    public Folder saveFolder(Folder folder) {
        return folderRepository.save(folder);
    }

    public void deleteFolder(String folderName) {
        Folder folder = getFolderByName(folderName);
        folderRepository.delete(folder);
    }

    public List<Folder> getFoldersForUser(User user) {
        return folderRepository.findAll().stream()
                .filter(f -> f.getUser().getId().equals(user.getId()))
                .toList();
    }
}