package com.foodly.Models;

import java.time.LocalDateTime;

public class Usuario {

    private Integer id;
    private String nome;
    private String email;
    private String senhaHash;
    private String telefone;
    private String tipoUsuario;
    private LocalDateTime criadoEm;
    private String fotoPerfil;

    public Usuario() {
    }

    public Usuario(Integer id,
                   String nome,
                   String email,
                   String senhaHash,
                   String telefone,
                   String tipoUsuario,
                   LocalDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.telefone = telefone;
        this.tipoUsuario = tipoUsuario;
        this.criadoEm = criadoEm;
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getFotoPerfil() { 
        return fotoPerfil; 
    }
    
    public void setFotoPerfil(String fotoPerfil) { 
        this.fotoPerfil = fotoPerfil; 
    }
}
