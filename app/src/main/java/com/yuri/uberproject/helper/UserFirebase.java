package com.yuri.uberproject.helper;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.yuri.uberproject.activity.DriverActivity;
import com.yuri.uberproject.activity.PassengerActivity;
import com.yuri.uberproject.config.ConfigurationFirebase;
import com.yuri.uberproject.emuns.TypeUser;
import com.yuri.uberproject.model.User;

public class UserFirebase {

    public static boolean updateNameUser(String name){
        try {
            FirebaseUser user = getUserCurrent();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name).build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (isErrorInUpdateNameUser(task)){
                        Log.d("Erro Perfil: ", "Erro ao atualizar nome! ") ;
                    }
                }
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static FirebaseUser getUserCurrent(){
        FirebaseAuth user = ConfigurationFirebase.getAuth();
        return user.getCurrentUser();
    }

    private static boolean isErrorInUpdateNameUser(Task<Void> task){
        return !task.isSuccessful();
    }

    public static void redirectsUserLogged(final Activity activity){
        FirebaseUser user = getUserCurrent();
        if(user != null){

            DatabaseReference ref = ConfigurationFirebase
                    .getDatabaseReference()
                    .child("usuarios")
                    .child(getIdentifierUser());

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    TypeUser typeUser = user.getTypeUser();
                    if(typeUser.equals(TypeUser.DRIVER)){
                        activity.startActivity(new Intent(activity, DriverActivity.class));
                    }else {
                        activity.startActivity(new Intent(activity, PassengerActivity.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public static String getIdentifierUser(){
        return getUserCurrent().getUid();
    }

}
