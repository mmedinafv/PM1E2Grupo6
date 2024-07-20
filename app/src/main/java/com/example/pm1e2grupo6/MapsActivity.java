package com.example.pm1e2grupo6;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import client.Client;
import client.Servicios;
import model.ApiResponse;
import model.Contacto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, OnMapsSdkInitializedCallback {

    private Servicios servicio = new Client().getInstancia().getServicios();
    private GoogleMap mMap;
    private TextView lblNombre, lblLatitud, lblLongitud;
    private Button btnInitTravel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_maps);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        MapView mapFragment = findViewById(R.id.map);
        lblNombre = findViewById(R.id.lblNombre);
        lblLatitud = findViewById(R.id.lblLatitud);
        lblLongitud = findViewById(R.id.lblLongitud);
        btnInitTravel = findViewById(R.id.btnInitTravel);
        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST, this);

        if (mapFragment != null) {
            mapFragment.onCreate(savedInstanceState);
            mapFragment.getMapAsync(this);
            mapFragment.onStart();
        }

        int id = getIntent().getIntExtra("id", 0);
        if(id != 0){
            getContacto(id);
        }

        btnInitTravel.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Inicar Viaje")
                    .setMessage("¿Desea inicar un viaje?")
                    .setPositiveButton("Si", (dialog, which) -> {
                        Uri locateIntentUri = Uri.parse("google.navigation:q=" + lblLatitud.getText() + "," + lblLongitud.getText());
                        Intent travelIntent = new Intent(Intent.ACTION_VIEW, locateIntentUri);
                        travelIntent.setPackage("com.google.android.apps.maps");
                        startActivity(travelIntent);
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                    })
                    .show();

        });

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Aquí puedes agregar código adicional si es necesario cuando el mapa esté listo
    }


    @Override
    public void onMapsSdkInitialized(@NonNull MapsInitializer.Renderer renderer) {

    }

    private void getContacto(int id){
        servicio.getContactosByID(id).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    //Deserialización de la respuesta
                    Gson gson = new Gson();
                    String jsonContext = gson.toJson(response.body().getData().getContent());
                    Type type = new TypeToken<List<Contacto>>(){}.getType();
                    List<Contacto> contactos = gson.fromJson(jsonContext, type);
                    lblNombre.setText(contactos.get(0).getNombre());
                    lblLatitud.setText(String.valueOf(contactos.get(0).getLatitud()));
                    lblLongitud.setText(String.valueOf(contactos.get(0).getLongitud()));
                    double latitude = Double.parseDouble(String.valueOf(contactos.get(0).getLatitud()));
                    double longitude = Double.parseDouble(String.valueOf(contactos.get(0).getLongitud()));

                    if (mMap != null) {
                        LatLng location = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(location).title("Marcador"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
                    }
                } else {
                    Log.e("RolesServiceImpl", "Error en la petición");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("RolesServiceImpl", "Error en la petición", t);
            }
        });
    }
}
