package com.example.services;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.config.SecurityUtils;
import com.example.models.File;
import com.example.models.Folder;
import com.example.repositories.FileRepository;
import com.example.repositories.FolderRepository;

@Service
public class FileService {

    private final FolderRepository folderRepository;
    
    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository, FolderRepository folderRepository) {
        this.fileRepository = fileRepository;
        this.folderRepository = folderRepository;
    }

    public List<File> getAllFiles() {
        String ownerId = SecurityUtils.currentUserId();
        return fileRepository.findAllByOwnerId(ownerId);
    }

    public List<File> getFilesByFolder(Folder folder) {
        String ownerId = SecurityUtils.currentUserId();
        return fileRepository.findByFolder(folder, ownerId);
    }

    public Optional<File> getFileByName(String name) {
        String ownerId = SecurityUtils.currentUserId();
        return fileRepository.findByName(name, ownerId);
    }

    public List<File> getFilesByKeyword(String keyword) {
        String ownerId = SecurityUtils.currentUserId();
        return fileRepository.findByNameContaining(keyword, ownerId);
    }

    public File createFile(String name, Folder folder, byte[] data) {
        String ownerId = SecurityUtils.currentUserId();
        File file = new File(name, ownerId, folder, data);
        return fileRepository.save(file);
    }

    public File saveFile(String name, String folderName, MultipartFile multiPart) throws IOException {
        String ownerId = SecurityUtils.currentUserId();

        Folder folder = folderRepository.findByName(folderName, ownerId);
        if (folder == null) {
            throw new IllegalArgumentException("Folder not found: " + folderName);
        }

        return fileRepository.save(new File(name, ownerId, folder, multiPart.getBytes()));
    }

    public void deleteFile(String fileName) {
        String ownerId = SecurityUtils.currentUserId();
        File file = fileRepository.findByName(fileName, ownerId)
            .orElseThrow(() -> new IllegalArgumentException("File not found: " + fileName));
        fileRepository.delete(file);
    }
}
