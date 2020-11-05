package com.yuri.uberproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.yuri.uberproject.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao = FirebaseAuth.getInstance();
    private TextInputEditText campoEmail, campoSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initComponents();
    }

    private void initComponents(){
        campoEmail = findViewById(R.id.textoEmail);
        campoSenha = findViewById(R.id.textoSenha);
    }

    public void logarButtonClick(View view){
        logarUsuario();
    }

    private void logarUsuario(){
        String email = String.valueOf(campoEmail.getText());
        String senha = String.valueOf(campoSenha.getText());


        autenticacao.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            abrirTelaPrincipal();
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

    private void abrirTelaPrincipal(){
        startActivity(new Intent(this, MapsActivity.class));
    }
}