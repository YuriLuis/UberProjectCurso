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
import com.yuri.uberproject.config.ConfiguracaoFirebase;
import com.yuri.uberproject.emuns.TipoUsuario;
import com.yuri.uberproject.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText campoNome, campoEmail, campoSenha;
    private RadioGroup radioGroup;
    private RadioButton radioButtonPassageiro, radioButtonMotorista;
    private TipoUsuario tipoUsuario = TipoUsuario.NÃO_INFORMADO;
    private FirebaseAuth autenticacao = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        initComponents();
        verificaSePassageiroOuMotorista();
    }

    private void initComponents(){
        campoNome = findViewById(R.id.textoNomeCompleto);
        campoSenha = findViewById(R.id.textoSenhaCad);
        campoEmail = findViewById(R.id.textoEmailCad);
        radioGroup = findViewById(R.id.radioGroup2);
        radioButtonMotorista = findViewById(R.id.radioButtonMotorista);
        radioButtonPassageiro = findViewById(R.id.radioButtonPassageiro);
    }

    public void cadastrarUsuario(View view){
        if (validaCamposCadastro()){
            String email = String.valueOf(campoEmail.getText());
            String senha = String.valueOf(campoSenha.getText());
            String nome = String.valueOf(campoNome.getText());
            verificaSePassageiroOuMotorista();

            Usuario usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setEmail(email);
            usuario.setSenha(senha);
            usuario.setTipoUsuario(this.tipoUsuario);

            autenticacao = ConfiguracaoFirebase.getAuth();
            autenticacao.createUserWithEmailAndPassword(
                    email, senha
            ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(CadastroActivity.this, "Sucesso ao cadastrar usuário!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(CadastroActivity.this, "Erro ao cadastrar usuário!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            finish();
        }
    }

    private boolean validaCamposCadastro(){
        String email = String.valueOf(campoEmail.getText());
        String senha = String.valueOf(campoSenha.getText());
        String nome = String.valueOf(campoNome.getText());

        if(isCampoVazio(nome)){
            return false;
        }else if(isCampoVazio(senha)){
            return false;
        }else if(isCampoVazio(email)){
            return false;
        }else if(tipoUsuarioInvalido(this.tipoUsuario)){
            return false;
        }else{
            return true;
        }
    }

    private boolean isCampoVazio(String campo) {
        return campo.isEmpty();
    }

    private void verificaSePassageiroOuMotorista(){

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (isMotorista()){
                    tipoUsuario = TipoUsuario.MOTORISTA;
                }else if(isPassageiro()){
                    tipoUsuario = TipoUsuario.PASSAGEIRO;
                }else {
                    tipoUsuario = TipoUsuario.NÃO_INFORMADO;
                }
            }
        });
    }

    private boolean tipoUsuarioInvalido(TipoUsuario tipoUsuario){
        return tipoUsuario.equals(TipoUsuario.NÃO_INFORMADO);
    }

    private boolean isMotorista(){
        return radioButtonMotorista.isChecked();
    }

    private boolean isPassageiro(){
        return radioButtonPassageiro.isChecked();
    }
}