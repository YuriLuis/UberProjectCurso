package com.yuri.uberproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yuri.uberproject.R;
import com.yuri.uberproject.adapter.AdapterRequisitons;
import com.yuri.uberproject.config.ConfigurationFirebase;
import com.yuri.uberproject.helper.UserFirebase;
import com.yuri.uberproject.model.Requisition;
import com.yuri.uberproject.model.User;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;

public class DriverActivity extends AppCompatActivity {

    private FirebaseAuth auth = ConfigurationFirebase.getAuth();
    private DatabaseReference databaseReference = ConfigurationFirebase.getDatabaseReference();

    private RecyclerView recyclerViewRequisitions;
    private TextView textResultado;
    private List<Requisition> listRequisitions = new ArrayList<>();
    private User driver = UserFirebase.getUserLoggedData();
    private AdapterRequisitons adapter = new AdapterRequisitons(listRequisitions, DriverActivity.this, this.driver);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        initComponents();

    }

    private void initComponents(){
        getSupportActionBar().setTitle("Requisições");

        recyclerViewRequisitions = findViewById(R.id.recyclerRequisitons);
        textResultado = findViewById(R.id.textResultado);

        recuperarRequisicoes();
        configuraRecyclerViewRequisitions(adapter);
    }

    private void recuperarRequisicoes(){
        DatabaseReference requisicoes = databaseReference.child("requisitions");
        Query requisicaoPesquisa = requisicoes.orderByChild("status")
                .equalTo(Requisition.STATUS_AGUARDANDO);
        requisicaoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    textResultado.setVisibility(View.GONE);
                    recyclerViewRequisitions.setVisibility(View.VISIBLE);
                }else{
                    textResultado.setVisibility(View.VISIBLE);
                    recyclerViewRequisitions.setVisibility(View.GONE);
                }

                for (DataSnapshot ds : snapshot.getChildren()){
                    Requisition requisition = ds.getValue(Requisition.class);
                    listRequisitions.add(requisition);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSair:
                auth.signOut();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void configuraRecyclerViewRequisitions(AdapterRequisitons adapterRequisitons){
        if(adapterRequisitons != null){
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DriverActivity.this);
            recyclerViewRequisitions.setLayoutManager(layoutManager);
            recyclerViewRequisitions.setHasFixedSize(true);
            recyclerViewRequisitions.setAdapter(adapterRequisitons);
        }
    }

    private AdapterRequisitons createAdapter(){
        AdapterRequisitons adpter = new AdapterRequisitons(
                this.listRequisitions,
                DriverActivity.this,
                this.driver
        );
        return adpter;
    }
}