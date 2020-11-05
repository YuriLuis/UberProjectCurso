package com.yuri.uberproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.yuri.uberproject.R;
import com.yuri.uberproject.activity.LoginActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void entrar(View view){
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void cadastrar(View view){
        startActivity(new Intent(this, CadastroActivity.class));
    }
}