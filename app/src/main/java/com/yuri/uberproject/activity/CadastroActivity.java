package com.yuri.uberproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.yuri.uberproject.R;
import com.yuri.uberproject.config.ConfigurationFirebase;
import com.yuri.uberproject.emuns.TypeUser;
import com.yuri.uberproject.model.User;

public class CadastroActivity extends AppCompatActivity {

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
            String senha = String.valueOf(fieldPassword.getText());
            String nome = String.valueOf(fieldName.getText());
            checksIfPassengerOrDriver();

            final User user = new User();
            user.setName(nome);
            user.setEmail(email);
            user.setPassword(senha);
            user.setTypeUser(this.typeUser);

            auth = ConfigurationFirebase.getAuth();
            auth.createUserWithEmailAndPassword(
                    email, senha
            ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                            saveUserFirebase(user, task);
                        Toast.makeText(CadastroActivity.this, "Sucesso ao cadastrar usuário!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(CadastroActivity.this, "Erro ao cadastrar usuário!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            finish();
        }
    }

    private void saveUserFirebase(User user, Task<AuthResult> task){
        String idUser = task.getResult().getUser().getUid();
        user.setId(idUser);
        user.save();

    }

    private boolean validatesFieldsRegisterUser(){
        String email = String.valueOf(fieldEmail.getText());
        String password = String.valueOf(fieldPassword.getText());
        String name = String.valueOf(fieldName.getText());

        if(isFieldsEmpty(name)){
            return false;
        }else if(isFieldsEmpty(password)){
            return false;
        }else if(isFieldsEmpty(email)){
            return false;
        }else if(typeUserInvalid(this.typeUser)){
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