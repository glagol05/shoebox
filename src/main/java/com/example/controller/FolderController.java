package com.example.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.models.Folder;
import com.example.models.User;
import com.example.services.FileService;
import com.example.services.FolderService;
import com.example.services.UserService;

@RestController
@RequestMapping("/folder")
public class FolderController {

    private final FolderService folderService;
    private final FileService fileService;
    private final UserService userService;

    public FolderController(FolderService folderService, FileService fileService, UserService userService) {
        this.folderService = folderService;
        this.fileService = fileService;
        this.userService = userService;
    }

    @GetMapping
    public CollectionModel<EntityModel<Folder>> getAllFolders(Principal principal) {
        User user = userService.findOrCreate(principal);

        List<EntityModel<Folder>> folders = folderService.getFoldersForUser(user).stream()
                .map(folder -> EntityModel.of(folder,
                        linkTo(methodOn(FolderController.class).getFolderByName(folder.getName(), principal)).withSelfRel(),
                        linkTo(methodOn(FolderController.class).deleteFolder(folder.getName(), principal)).withRel("delete")
                ))
                .toList();

        return CollectionModel.of(folders,
                linkTo(methodOn(FolderController.class).getAllFolders(principal)).withSelfRel()
        );
    }

    @GetMapping("/byname/{folderName}")
    public EntityModel<Folder> getFolderByName(@PathVariable String folderName, Principal principal) {
        User user = userService.findOrCreate(principal);
        Folder folder = folderService.getFolderByName(folderName);

        if (folder == null || !folder.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder not found");
        }

        return EntityModel.of(folder,
                linkTo(methodOn(FolderController.class).getFolderByName(folder.getName(), principal)).withSelfRel(),
                linkTo(methodOn(FolderController.class).deleteFolder(folder.getName(), principal)).withRel("delete")
        );
    }

    @PostMapping("/create")
    public EntityModel<Folder> createFolder(@RequestParam String name, Principal principal) {
        User user = userService.findOrCreate(principal);
        Folder folder = folderService.createFolder(name, user);

        return EntityModel.of(folder,
                linkTo(methodOn(FolderController.class).getFolderByName(folder.getName(), principal)).withSelfRel(),
                linkTo(methodOn(FolderController.class).deleteFolder(folder.getName(), principal)).withRel("delete")
        );
    }

    @DeleteMapping("/delete/{folderName}")
    public ResponseEntity<Void> deleteFolder(@PathVariable String folderName, Principal principal) {
        User user = userService.findOrCreate(principal);
        Folder folder = folderService.getFolderByName(folderName);

        if (folder == null || !folder.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        folderService.deleteFolder(folderName);
        return ResponseEntity.noContent().build();
    }
}