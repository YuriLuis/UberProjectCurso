package com.yuri.uberproject.model;

import com.google.firebase.database.DatabaseReference;
import com.yuri.uberproject.config.ConfigurationFirebase;

public class Requisition {

    private String id;
    private String status;
    private User passenger;
    private User driver;
    private Destiny destiny;

    public static final String STATUS_AGUARDANDO = "aguardando";
    public static final String STATUS_A_CAMINHO = "acaminho";
    public static final String STATUS_VIAGEM = "viagem";
    public static final String STATUS_FINALIZADA = "finalizada";

    public void saveRequisiton(){
        DatabaseReference databaseReference = ConfigurationFirebase.getDatabaseReference();
        DatabaseReference requisitions = databaseReference.child("requisitions");

        String idRequisition = requisitions.push().getKey();
        setId(idRequisition);

        requisitions.child(getId()).setValue(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getPassenger() {
        return passenger;
    }

    public void setPassenger(User passenger) {
        this.passenger = passenger;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public Destiny getDestiny() {
        return destiny;
    }

    public void setDestiny(Destiny destiny) {
        this.destiny = destiny;
    }
}
