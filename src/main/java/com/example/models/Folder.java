package com.example.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "folders", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "owner_id"})
})
public class Folder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    private String name;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(nullable = false, updatable = false)
    private String ownerId;

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<File> files = new ArrayList<>();

    protected Folder() {}

    public Folder(String name, String ownerId) {
        this.name = name;
        this.ownerId = ownerId;
    }

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
    }

    public void addFile(File file) {
        files.add(file);
        file.setFolder(this);
    }

    public void removeFile(File file) {
        files.remove(file);
        file.setFolder(null);
    }

    public UUID getId() { return id; }

    public String getName() { return name; }

    public LocalDateTime getCreationDate() {return creationDate; }

    public String getOwnerId() { return ownerId; }

    public List<File> getFiles() { return files; }
}
