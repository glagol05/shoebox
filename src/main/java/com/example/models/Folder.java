package com.example.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "folders", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})
})
public class Folder {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<File> files = new ArrayList<>();

    protected Folder() {}

    public Folder(String name, User user) {
        this.name = name;
        this.user = user;
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
    public LocalDateTime getCreationDate() { return creationDate; }
    public List<File> getFiles() { return files; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}