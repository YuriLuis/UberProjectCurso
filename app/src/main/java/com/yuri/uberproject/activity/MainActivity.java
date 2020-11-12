package com.yuri.uberproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.yuri.uberproject.R;
import com.yuri.uberproject.config.ConfigurationFirebase;
import com.yuri.uberproject.helper.UserFirebase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //auth = ConfigurationFirebase.getAuth();
        //auth.signOut();
    }


    public void loginActivity(View view){
        startActivity(new Intent(this, LoginActivity.class));

    }

    public void registerActivity(View view){
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        UserFirebase.redirectsUserLogged(MainActivity.this);
    }
}