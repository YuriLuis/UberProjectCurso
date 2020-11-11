package com.yuri.uberproject.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.yuri.uberproject.config.ConfigurationFirebase;
import com.yuri.uberproject.emuns.TypeUser;

public class User {

    private String id;
    private String name;
    private String email;
    private String password;
    private TypeUser typeUser;

    public User(){

    }

    public void save(){
        DatabaseReference databaseReference = ConfigurationFirebase.getDatabaseReference();
        DatabaseReference usuarios = databaseReference.child("usuarios").child(getId());
        usuarios.setValue(this);
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public TypeUser getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(TypeUser typeUser) {
        this.typeUser = typeUser;
    }
}
