package com.example.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.models.File;
import com.example.models.Folder;
import com.example.services.FileService;
import com.example.services.FolderService;


@RestController
@RequestMapping("/files")
public class FileController {
    
    private final FileService fileService;
    private final FolderService folderService;

    public FileController(FileService fileService, FolderService folderService) {
        this.fileService = fileService;
        this.folderService = folderService;
    }

    @GetMapping
    public List<File> getAllFiles() {
        return fileService.getAllFiles();
    }

    @PostMapping("/upload")
    public File uploadFile(@RequestParam("file") MultipartFile multipart,
                        @RequestParam("folderName") String folderName) throws Exception {
        return fileService.saveFile(multipart.getOriginalFilename(), folderName, multipart);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {

        File file = fileService.getFileByName(fileName)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file.getName() + "\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(file.getData());
    }

    @DeleteMapping("/delete/{fileName}")
    public void deleteFile(@PathVariable String fileName) {
        fileService.deleteFile(fileName);
    }

    @GetMapping("/folder/{folderName}")
    public List<File> getFilesInFolder(@PathVariable String folderName) {
        Folder folder = folderService.getFolderByName(folderName);
        return fileService.getFilesByFolder(folder);
    }
    
    @GetMapping("/byname/{fileName}")
    public Optional<File> getFileByName(@PathVariable String fileName) {
        return fileService.getFileByName(fileName);
    }

    @GetMapping("/search/{keyword}")
    public List<File> getFilesByKeyword(@PathVariable String keyword) {
        return fileService.getFilesByKeyword(keyword);
    }
}
