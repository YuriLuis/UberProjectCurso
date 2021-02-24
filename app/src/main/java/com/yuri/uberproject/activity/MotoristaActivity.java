package com.yuri.uberproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yuri.uberproject.R;
import com.yuri.uberproject.adapter.RequisicoesAdapter;
import com.yuri.uberproject.config.ConfigurationFirebase;
import com.yuri.uberproject.helper.RecyclerItemClickListener;
import com.yuri.uberproject.helper.UserFirebase;
import com.yuri.uberproject.model.Requisicao;
import com.yuri.uberproject.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class MotoristaActivity extends AppCompatActivity {

    private FirebaseAuth auth = ConfigurationFirebase.getAuth();
    private DatabaseReference databaseReference = ConfigurationFirebase.getDatabaseReference();

    private RecyclerView recyclerViewRequisitions;
    private TextView textResultado;
    private List<Requisicao> listaRequisicoes = new ArrayList<>();
    private Usuario motorista = UserFirebase.getUserLoggedData();
    private RequisicoesAdapter adapter = new RequisicoesAdapter(listaRequisicoes, MotoristaActivity.this, this.motorista);
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motorista);
        initComponents();

        getLocalGpsMotorista();
    }

    private void getLocalGpsMotorista() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                String latitude =  String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());

                if( latitudeELongetudeIsOk(latitude,longitude)){
                    motorista.setLatitude(latitude);
                    motorista.setLongitude(longitude);
                    //Não recebe mais os dados de update de localização do usuário!!!
                    locationManager.removeUpdates(locationListener);
                    adapter.notifyDataSetChanged();
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(MotoristaActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    10,
                    locationListener
            );
        }else {
            new AlertDialog.Builder(MotoristaActivity.this)
                    .setTitle("Ative a opção GPS do dispositivo!")
                    .setMessage("Erro ao tentar buscar localização, GPS está desativado! ")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onResume();
                        }
                    })
                    .show();
        }

    }

    private boolean latitudeELongetudeIsOk(String a, String b){
        return !a.isEmpty() && !b.isEmpty();
    }

    private void initComponents(){
        getSupportActionBar().setTitle("Requisições");

        recyclerViewRequisitions = findViewById(R.id.recyclerRequisitons);
        textResultado = findViewById(R.id.textResultado);

        recuperarRequisicoes();
        configuraRecyclerViewRequisitions(adapter);
        eventClickRecyclerView();
    }

    private void recuperarRequisicoes(){
        DatabaseReference requisicoes = databaseReference.child("requisitions");
        Query requisicaoPesquisa = requisicoes.orderByChild("status")
                .equalTo(Requisicao.STATUS_AGUARDANDO);
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
                    Requisicao requisicao = ds.getValue(Requisicao.class);
                    listaRequisicoes.add(requisicao);
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

    private void configuraRecyclerViewRequisitions(RequisicoesAdapter requisicoesAdapter){
        if(requisicoesAdapter != null){
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MotoristaActivity.this);
            recyclerViewRequisitions.setLayoutManager(layoutManager);
            recyclerViewRequisitions.setHasFixedSize(true);
            recyclerViewRequisitions.setAdapter(requisicoesAdapter);
        }
    }

    private void eventClickRecyclerView(){
        recyclerViewRequisitions.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        MotoristaActivity.this,
                        recyclerViewRequisitions,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Requisicao requisicao = listaRequisicoes.get(position);
                                Intent i = new Intent(MotoristaActivity.this, CorridaActivity.class);
                                i.putExtra("idRequisicao", requisicao.getId());
                                i.putExtra("motorista", motorista);
                                startActivity(i);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );
    }

}