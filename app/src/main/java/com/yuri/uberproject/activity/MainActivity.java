package com.yuri.uberproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.yuri.uberproject.R;
import com.yuri.uberproject.config.ConfigurationFirebase;
import com.yuri.uberproject.helper.Permissoes;
import com.yuri.uberproject.helper.UserFirebase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private String[] permisions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        checkPermission();
        //auth = ConfigurationFirebase.getAuth();
        //auth.signOut();
    }

    @Override
    protected void onStart() {
        super.onStart();
        UserFirebase.redirectsUserLogged(MainActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int permissionResult : grantResults){
            if(permissionResult == PackageManager.PERMISSION_DENIED){
                alertDialogPermission();
            }
        }
    }

    public void loginActivity(View view){
        startActivity(new Intent(this, LoginActivity.class));

    }

    public void registerActivity(View view){
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void checkPermission(){
        Permissoes.validarPermissoes(permisions, MainActivity.this, 1);
    }

    private void alertDialogPermission(){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Permissões Negadas!")
                .setMessage("Para utilizar o app é necessário aceitar as permissões!")
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                    }
                }).show();
    }
}