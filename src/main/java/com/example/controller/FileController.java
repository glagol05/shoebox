package com.example.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
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
import com.example.models.User;
import com.example.services.FileService;
import com.example.services.FolderService;
import com.example.services.UserService;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;
    private final FolderService folderService;
    private final UserService userService;

    public FileController(FileService fileService, FolderService folderService, UserService userService) {
        this.fileService = fileService;
        this.folderService = folderService;
        this.userService = userService;
    }

    @GetMapping
    public CollectionModel<EntityModel<File>> getAllFiles(Principal principal) {
        User user = userService.findOrCreate(principal);

        List<EntityModel<File>> files = fileService.getFilesForUser(user).stream()
                .map(file -> EntityModel.of(file,
                        linkTo(methodOn(FileController.class).getFileByName(file.getName(), principal)).withSelfRel(),
                        linkTo(methodOn(FileController.class).downloadFile(file.getName(), principal)).withRel("download"),
                        linkTo(methodOn(FileController.class).deleteFile(file.getName(), principal)).withRel("delete")
                ))
                .toList();

        return CollectionModel.of(files,
                linkTo(methodOn(FileController.class).getAllFiles(principal)).withSelfRel()
        );
    }

    @GetMapping("/byname/{fileName}")
    public EntityModel<File> getFileByName(@PathVariable String fileName, Principal principal) {
        User user = userService.findOrCreate(principal);
        File file = fileService.getFileByName(fileName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!file.getFolder().getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not allowed to access this file");
        }

        return EntityModel.of(file,
                linkTo(methodOn(FileController.class).getFileByName(file.getName(), principal)).withSelfRel(),
                linkTo(methodOn(FileController.class).downloadFile(file.getName(), principal)).withRel("download"),
                linkTo(methodOn(FileController.class).deleteFile(file.getName(), principal)).withRel("delete")
        );
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName, Principal principal) {
        User user = userService.findOrCreate(principal);
        File file = fileService.getFileByName(fileName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!file.getFolder().getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not allowed to download this file");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file.getData());
    }

    @PostMapping("/upload")
    public File uploadFile(@RequestParam("file") MultipartFile multipart, @RequestParam("folderName") String folderName, Principal principal) throws Exception {

        User user = userService.findOrCreate(principal);
        Folder folder = folderService.getFolderByName(folderName);

        if (folder == null || !folder.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cannot upload to this folder");
        }

        File file = fileService.createFile(multipart.getOriginalFilename(), folder, multipart.getBytes());
        folder.addFile(file);

        return file;
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileName, Principal principal) {
        User user = userService.findOrCreate(principal);
        File file = fileService.getFileByName(fileName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!file.getFolder().getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not allowed to delete this file");
        }

        fileService.deleteFile(fileName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/{keyword}")
    public CollectionModel<EntityModel<File>> getFilesByKeyword(@PathVariable String keyword, Principal principal) {
        User user = userService.findOrCreate(principal);

        List<EntityModel<File>> files = fileService.getFilesByKeyword(keyword).stream()
                .filter(f -> f.getFolder().getUser().getId().equals(user.getId())) // only user's files
                .map(file -> EntityModel.of(file,
                        linkTo(methodOn(FileController.class).getFileByName(file.getName(), principal)).withSelfRel(),
                        linkTo(methodOn(FileController.class).downloadFile(file.getName(), principal)).withRel("download"),
                        linkTo(methodOn(FileController.class).deleteFile(file.getName(), principal)).withRel("delete")
                ))
                .toList();

        return CollectionModel.of(files,
                linkTo(methodOn(FileController.class).getFilesByKeyword(keyword, principal)).withSelfRel()
        );
    }
}