package com.yuri.uberproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.yuri.uberproject.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private TextInputEditText fieldEmail, fieldPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initComponents();
    }

    private void initComponents(){
        fieldEmail = findViewById(R.id.textoEmail);
        fieldPassword = findViewById(R.id.textoSenha);
    }

    public void logInClickButton(View view){
        logInUser();
    }

    private void logInUser(){
        String email = String.valueOf(fieldEmail.getText());
        String password = String.valueOf(fieldPassword.getText());

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            openMainActivity();
                        }else {
                          String erro = "";
                          try {
                              throw task.getException();
                          }catch (FirebaseAuthInvalidUserException e){
                                erro = "Email ou senha não correspondem a um usuário cadastrado!";
                          }catch (FirebaseAuthInvalidCredentialsException e){
                              erro = "Usuário não está cadastrado!";
                          }catch (Exception e) {
                              erro = "Erro ao entrar" + e.getMessage();
                              e.printStackTrace();
                          }

                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("Erro")
                                    .setMessage(erro)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                        }
                    }
                });
    }

    private void openMainActivity(){
        startActivity(new Intent(this, MapsActivity.class));
    }
}