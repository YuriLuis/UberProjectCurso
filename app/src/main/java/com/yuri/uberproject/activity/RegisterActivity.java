package com.yuri.uberproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.yuri.uberproject.R;
import com.yuri.uberproject.config.ConfigurationFirebase;
import com.yuri.uberproject.emuns.TypeUser;
import com.yuri.uberproject.helper.UserFirebase;
import com.yuri.uberproject.model.User;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText fieldName, fieldEmail, fieldPassword;
    private RadioGroup radioGroup;
    private RadioButton radioButtonPassageiro, radioButtonMotorista;
    private TypeUser typeUser = TypeUser.UNINFORMED;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        initComponents();
        checksIfPassengerOrDriver();
    }

    private void initComponents(){
        fieldName = findViewById(R.id.textoNomeCompleto);
        fieldPassword = findViewById(R.id.textoSenhaCad);
        fieldEmail = findViewById(R.id.textoEmailCad);
        radioGroup = findViewById(R.id.radioGroup2);
        radioButtonMotorista = findViewById(R.id.radioButtonMotorista);
        radioButtonPassageiro = findViewById(R.id.radioButtonPassageiro);
    }

    public void registerUser(View view){
        if (validatesFieldsRegisterUser()){
            String email = String.valueOf(fieldEmail.getText());
            String password = String.valueOf(fieldPassword.getText());
            String name = String.valueOf(fieldName.getText());
            checksIfPassengerOrDriver();

            final User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setTypeUser(this.typeUser);

            auth = ConfigurationFirebase.getAuth();
            auth.createUserWithEmailAndPassword(
                    email, password
            ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    try{
                        if (task.isSuccessful()){
                            saveUserFirebase(user, task);
                            if(isDriver()){
                                openActivityDriver();
                            }else{
                                openActivityMaps();
                            }
                        }else {
                            String erro = "";
                            try {
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                erro = "Digite uma senha mais forte";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                erro = "Por favor, digite um e-mail válido!";
                            }catch (FirebaseAuthUserCollisionException e ){
                                erro = "Esta conta já foi cadastrada! ";
                            }catch (Exception e) {
                                erro = "Erro ao entrar" + e.getMessage();
                                e.printStackTrace();
                            }

                            new AlertDialog.Builder(RegisterActivity.this)
                                    .setTitle("Erro")
                                    .setMessage(erro)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                        }
                    }catch (RuntimeException e){
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void saveUserFirebase(User user, Task<AuthResult> task){
        String idUser = task.getResult().getUser().getUid();
        user.setId(idUser);
        user.saveUser();
        UserFirebase.updateNameUser(user.getName());
    }

    private void openActivityMaps(){
        startActivity(new Intent(RegisterActivity.this, PassengerActivity.class));
        Toast.makeText(RegisterActivity.this, "Sucess register a Passenger!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void openActivityDriver(){
        Toast.makeText(RegisterActivity.this, "Sucess register a driver!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(RegisterActivity.this, DriverActivity.class));
    }

    private boolean validatesFieldsRegisterUser(){
        String email = String.valueOf(fieldEmail.getText());
        String password = String.valueOf(fieldPassword.getText());
        String name = String.valueOf(fieldName.getText());

        if(isFieldsEmpty(name)){
            fieldName.setError("Informe o nome");
            return false;
        }else if(isFieldsEmpty(password)){
            fieldPassword.setError("Informe a senha!");
            return false;
        }else if(isFieldsEmpty(email)){
            fieldEmail.setError("Informe o email");
            return false;
        }else if(typeUserInvalid(this.typeUser)){
            new AlertDialog.Builder(RegisterActivity.this)
                    .setTitle("Erro")
                    .setMessage("Informe se Motorista ou Passageiro")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
            return false;
        }else{
            return true;
        }
    }

    private boolean isFieldsEmpty(String campo) {
        return campo.isEmpty();
    }

    private void checksIfPassengerOrDriver(){
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (isDriver()){
                    typeUser = TypeUser.DRIVER;
                }else if(isPassenger()){
                    typeUser = TypeUser.PASSENGER;
                }else {
                    typeUser = TypeUser.UNINFORMED;
                }
            }
        });
    }


    private boolean typeUserInvalid(TypeUser tipoUsuario){
        return tipoUsuario.equals(TypeUser.UNINFORMED);
    }

    private boolean isDriver(){
        return radioButtonMotorista.isChecked();
    }

    private boolean isPassenger(){
        return radioButtonPassageiro.isChecked();
    }
}