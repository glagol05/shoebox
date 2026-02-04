package com.example.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.models.File;
import com.example.models.Folder;
import com.example.repositories.FileRepository;

@Service
public class FileService {
    
    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public List<File> getAllFiles() {
        return fileRepository.findAll();
    }

    public List<File> getFilesByFolder(Folder folder) {
        return fileRepository.findByFolder(folder);
    }

    public Optional<File> getFileByName(String name) {
        return fileRepository.findByName(name);
    }

    public List<File> getFilesByKeyword(String keyword) {
        return fileRepository.findByNameContaining(keyword);
    }

    public File createFile(String name, Folder folder, byte[] data) {
        File file = new File(name, folder, data);
        return fileRepository.save(file);
    }

    public File saveFile(File file) {
        return fileRepository.save(file);
    }

    public void deleteFile(String fileName) {
        File file = fileRepository.findByName(fileName)
            .orElseThrow(() -> new IllegalArgumentException("File not found: " + fileName));
        fileRepository.delete(file);
    }
}
