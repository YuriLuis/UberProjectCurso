package com.yuri.uberproject.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.yuri.uberproject.BuildConfig;
import com.yuri.uberproject.R;
import com.yuri.uberproject.config.ConfigurationFirebase;
import com.yuri.uberproject.helper.UserFirebase;
import com.yuri.uberproject.model.Destiny;
import com.yuri.uberproject.model.Requisition;
import com.yuri.uberproject.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PassengerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private EditText destinyText;
    private LinearLayout linearLayout;
    private Button buttonCallUber;

    private GoogleMap mMap;
    private FirebaseAuth auth = ConfigurationFirebase.getAuth();
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng userPlace;
    private boolean uberCall = false;
    private DatabaseReference databaseReference;
    private Requisition requisition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Iniciar uma viagem");
        setSupportActionBar(toolbar);
        initComponents();
        clickButtonCallUber();
        checkStatusRequisition();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocationPassenger();
    }

    private void initComponents() {

        databaseReference = ConfigurationFirebase.getDatabaseReference();
        destinyText = findViewById(R.id.textDestino);
        buttonCallUber = findViewById(R.id.buttonChamarMotorista);
        linearLayout = findViewById(R.id.linearLayoutDestiny);
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
        getLocationPassenger();
        // Add a marker in Sydney and move the camera

    }

    private void checkStatusRequisition(){
        User user = UserFirebase.getUserLoggedData();
        DatabaseReference databaseReference = this.databaseReference.child("requisitions");
        final Query requisicaoPesquisa = databaseReference.orderByChild("passenger/id")
                .equalTo( user.getId());
        requisicaoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Requisition> list = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()){
                    list.add(ds.getValue(Requisition.class));
                }

                Collections.reverse(list);

                if(list.size() != 0){
                    requisition = list.get(0);
                    switch (requisition.getStatus()){
                        case Requisition.STATUS_AGUARDANDO :
                            linearLayout.setVisibility(View.GONE);
                            buttonCallUber.setText("Cancelar Uber");
                            uberCall = true;
                            break;
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void clickButtonCallUber(){
        buttonCallUber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!uberCall){
                    String destino = destinyText.getText().toString();
                    if (validateFieldDestiny()) {
                        Address address = getAddress(destino);
                        if (address != null) {
                            final Destiny destiny = new Destiny();
                            destiny.setCity(address.getSubAdminArea());
                            destiny.setZipCode(address.getPostalCode());
                            destiny.setBairro(address.getSubLocality());
                            destiny.setStreet(address.getThoroughfare());
                            destiny.setNumber(address.getFeatureName());
                            destiny.setLatitude(String.valueOf(address.getLatitude()));
                            destiny.setLongitude(String.valueOf(address.getLongitude()));

                            StringBuilder message = new StringBuilder();
                            message.append("Cidade: " + destiny.getCity());
                            message.append("\nRua: " + destiny.getStreet());
                            message.append("\nBairro: " + destiny.getBairro());
                            message.append("\nNúmero: " + destiny.getNumber());
                            message.append("\nCep: " + destiny.getZipCode());

                            new AlertDialog.Builder(PassengerActivity.this)
                                    .setTitle("Confirme seu endereço!")
                                    .setMessage(message)
                                    .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //Salva requisição para o motorista!
                                            saveRequisition(destiny);
                                            uberCall = true;
                                        }
                                    })
                                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).show();
                        }
                    } else {
                        Toast.makeText(PassengerActivity.this, "Informe o endereço de destino!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    uberCall = false;
                }
            }
        });
    }

    private void getLocationPassenger() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                userPlace = new LatLng(latitude, longitude);
                //LatLng sydney = new LatLng(-34, 151);
                mMap.clear();
                mMap.addMarker(
                        new MarkerOptions()
                                .position(userPlace)
                                .title("Meu Local!")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_emoji_people_black_18dp))
                );
                mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(userPlace, 18)
                );
            }
        };

        if (ActivityCompat.checkSelfPermission(PassengerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000,
                    10,
                    locationListener
            );
        }else {
            new AlertDialog.Builder(PassengerActivity.this)
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

    private void saveRequisition(Destiny destiny){
        Requisition requisition = new Requisition();
        saveDestinyInRequisition(destiny, requisition);

        User userPassenger = createUserForRequisition();

        requisition.setPassenger(userPassenger);
        requisition.setStatus(Requisition.STATUS_AGUARDANDO);
        requisition.saveRequisiton();

        linearLayout.setVisibility(View.GONE);
        buttonCallUber.setText(R.string.canceled_uber);
    }

    private User createUserForRequisition(){
        User userPassenger = UserFirebase.getUserLoggedData();
        if(userPassenger != null) {
            userPassenger.setLatitude( String.valueOf(userPlace.latitude));
            userPassenger.setLongitude(String.valueOf(userPlace.longitude));
        } else {
            if(BuildConfig.DEBUG){
                Log.v("Erro :" , "Erro ao criar Usuario para requisição! ");
            }
        }

        return userPassenger;
    }

    private void saveDestinyInRequisition(Destiny destiny, Requisition requisition){
        if(destiny != null && requisition != null){
            requisition.setDestiny(destiny);
        }
        else {
            if(BuildConfig.DEBUG){
                Log.v("Erro :" , "Erro ao salvar Destino na requisição! ");
            }
        }
    }

    private Address getAddress(String address) {
        Geocoder geocoder = new Geocoder(PassengerActivity.this, Locale.getDefault());
        try {

            List<Address> listAddress = geocoder.getFromLocationName(address, 1);

            if (validateListAddress(listAddress)) {
                Address address1 = listAddress.get(0);
                double latitude = address1.getLatitude();
                double longitude = address1.getLongitude();
                return address1;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private boolean validateFieldDestiny() {
        String address = String.valueOf( destinyText.getText());
        if (isFieldsEmpty(address)) {
            Toast.makeText(this, "Informe o endereço de destino!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateListAddress(List<Address> listAddress){
        return listAddress != null && listAddress.size() > 0;
    }

    private boolean isFieldsEmpty(String campo) {
        return campo.isEmpty();
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
}