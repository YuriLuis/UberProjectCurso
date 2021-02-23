package com.yuri.uberproject.model;

import com.google.firebase.database.DatabaseReference;
import com.yuri.uberproject.config.ConfigurationFirebase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Requisicao implements Serializable {

    private String id;
    private String status;
    private Usuario passenger;
    private Usuario driver;
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

    public void atualizar() {
        DatabaseReference firebaseRef = ConfigurationFirebase.getDatabaseReference();
        DatabaseReference requisicoes = firebaseRef.child("requisitions");

        DatabaseReference requisicao = requisicoes.child(getId());

        Map objeto = new HashMap();
        objeto.put("motorista", getDriver() );
        objeto.put("status", getStatus());

        requisicao.updateChildren( objeto );
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

    public Usuario getPassenger() {
        return passenger;
    }

    public void setPassenger(Usuario passenger) {
        this.passenger = passenger;
    }

    public Usuario getDriver() {
        return driver;
    }

    public void setDriver(Usuario driver) {
        this.driver = driver;
    }

    public Destiny getDestiny() {
        return destiny;
    }

    public void setDestiny(Destiny destiny) {
        this.destiny = destiny;
    }

}
