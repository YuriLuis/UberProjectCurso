package com.yuri.uberproject.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.yuri.uberproject.R;
import com.yuri.uberproject.config.ConfigurationFirebase;
import com.yuri.uberproject.model.Requisicao;
import com.yuri.uberproject.model.Usuario;

public class CorridaActivity extends AppCompatActivity implements OnMapReadyCallback {

    /**
     * Para Mostrar o mapa necessário:
     * 1º implements OnMapReadyCallback
     * 2º atributo mMap
     * 3º atributos locationManager,locationListener, meuLocal*/

    private Button botaoAceitaCorrida;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng localMotorista;
    private LatLng localPassageiro;
    private Requisicao requisicao = new Requisicao();
    private Usuario motorista;
    private Usuario passageiro;
    private String idRequisicao;
    private DatabaseReference firebaseRef = ConfigurationFirebase.getDatabaseReference();
    private Marker marcadorMotorista;
    private Marker marcadorPassageiro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corrida);

        recuperaDadosIntentExtras();
        iniciaComponentes();
    }

    private void recuperaDadosIntentExtras(){
        if (getIntent().getExtras().containsKey("idRequisicao")
        && getIntent().getExtras().containsKey("motorista")){
            Bundle extras = getIntent().getExtras();
            motorista = (Usuario) extras.getSerializable("motorista");
            idRequisicao = extras.getString("idRequisicao");
            verificaStatusRequisicao();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocalGpsUsuario();
        // Add a marker in Sydney and move the camera

    }

    private void getLocalGpsUsuario() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                localMotorista = new LatLng(latitude, longitude);
                //LatLng sydney = new LatLng(-34, 151);
                mMap.clear();
                mMap.addMarker(
                        new MarkerOptions()
                                .position(localMotorista)
                                .title("Local Motorista")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_directions_car_black_18dp))
                );
                mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(localMotorista, 18)
                );
            }
        };

        if (ActivityCompat.checkSelfPermission(CorridaActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000,
                    10,
                    locationListener
            );
        }else {
            new AlertDialog.Builder(CorridaActivity.this)
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

    private void verificaStatusRequisicao(){

        DatabaseReference requisicoes = firebaseRef.child("requisitions")
                .child( idRequisicao );
        requisicoes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Recupera requisição
                requisicao = dataSnapshot.getValue(Requisicao.class);
                passageiro = requisicao.getPassenger();
                localPassageiro = new LatLng(Double.parseDouble(passageiro.getLatitude()) , Double.parseDouble(passageiro.getLongitude()));

                switch ( requisicao.getStatus() ){
                    case Requisicao.STATUS_AGUARDANDO :
                        requisicaoAguardando();
                        break;
                    case Requisicao.STATUS_A_CAMINHO :
                        requisicaoACaminho();
                        break;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void requisicaoAguardando(){
        botaoAceitaCorrida.setText("Aceitar corrida");
    }

    private void requisicaoACaminho(){
        botaoAceitaCorrida.setText("A caminho do passageiro");
        //Exibe marcador do motorista
        adicionaMarcadorMotorista(localMotorista, motorista.getName() );

        //Exibe marcador passageiro
        adicionaMarcadorPassageiro(localPassageiro, passageiro.getName());

        //Centralizar dois marcadores
        centralizarDoisMarcadores(marcadorMotorista, marcadorPassageiro);

    }

    private void adicionaMarcadorMotorista(LatLng localizacao, String titulo){

        if( marcadorMotorista != null )
            marcadorMotorista.remove();

        marcadorMotorista = mMap.addMarker(
                new MarkerOptions()
                        .position(localizacao)
                        .title(titulo)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_directions_car_black_18dp))
        );

    }

    private void adicionaMarcadorPassageiro(LatLng localizacao, String titulo){

        if( marcadorPassageiro != null )
            marcadorPassageiro.remove();

        marcadorPassageiro = mMap.addMarker(
                new MarkerOptions()
                        .position(localizacao)
                        .title(titulo)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_emoji_people_black_18dp))
        );

    }

    private void centralizarDoisMarcadores(Marker marcador1, Marker marcador2){

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include( marcador1.getPosition() );
        builder.include( marcador2.getPosition() );

        LatLngBounds bounds = builder.build();

        int largura = getResources().getDisplayMetrics().widthPixels;
        int altura = getResources().getDisplayMetrics().heightPixels;
        int espacoInterno = (int) (largura * 0.20);

        mMap.moveCamera(
                CameraUpdateFactory.newLatLngBounds(bounds,largura,altura,espacoInterno)
        );

    }


    private void iniciaComponentes(){
        botaoAceitaCorrida = findViewById(R.id.botaoAceitaCorrida);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        aceitaCorridaEventoClickBotao();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    private void aceitaCorridaEventoClickBotao(){
        botaoAceitaCorrida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requisicao.setId(idRequisicao);
                requisicao.setDriver(motorista);
                requisicao.setStatus(Requisicao.STATUS_A_CAMINHO);
                requisicao.atualizar();
            }
        });
    }
}