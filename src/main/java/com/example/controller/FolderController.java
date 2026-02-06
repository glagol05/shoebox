package com.example.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.models.Folder;
import com.example.services.FileService;
import com.example.services.FolderService;



@RestController
@RequestMapping("/folder")
public class FolderController {
    
    private final FolderService folderService;
    private final FileService fileService;

    public FolderController(FolderService folderService, FileService fileService) {
        this.folderService = folderService;
        this.fileService = fileService;
    }

    @GetMapping
    public List<Folder> getAllFolders() {
        return folderService.getAllFolders();
    }

    @PostMapping("/create")
    public Folder createFolder(@RequestParam String name) {
        return folderService.createFolder(name);
    }
    
    @DeleteMapping("/delete/{folderName}")
    public void deleteFolder(@PathVariable String folderName) {
        folderService.deleteFolder(folderName);
    }
    
}
