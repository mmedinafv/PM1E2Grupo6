package com.example.pm1e2grupo6;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import client.Client;
import client.Servicios;
import model.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    private Servicios servicio = new Client().getInstancia().getServicios();
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int MICRONEPHONE_REQUEST_CODE = 200;

    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;

    private Button btnReproducir;
    private MediaPlayer mediaPlayer;

    private GoogleApiClient googleApiClient;
    private FusedLocationProviderClient fusedLocationProviderClient;


    Button btnContactosG, btnGrabar, btnGuardar;
    EditText nombre, telefono, latitud, longitud;

    private File audioFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        onConnected(null);
        nombre = findViewById(R.id.nombre);
        latitud = findViewById(R.id.latitud);
        longitud = findViewById(R.id.longitud);
        telefono = findViewById(R.id.telefono);
        btnGrabar = findViewById(R.id.btnGrabar);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnContactosG = findViewById(R.id.btnContactosG);
        ImageView imageView = findViewById(R.id.imageView);
        btnReproducir = findViewById(R.id.btnReproducir);

        btnReproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reproducirAudio();
            }
        });

      btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Guardar();
            }
        });

        btnContactosG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });

        btnGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording) {
                    deteneraudio();
                    btnGrabar.setText("Tomar grabacion");
                    imageView.setImageResource(R.drawable.img);

                } else {
                    Permisosaudio();
                    btnGrabar.setText("Detener grabacion");
                    imageView.setImageResource(R.drawable.micro);

                }
            }
        });
    }

        private void reproducirAudio() {
            if (audioFile != null && audioFile.exists()) {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                } else {
                    mediaPlayer.reset();
                }
                try {
                    mediaPlayer.setDataSource(audioFile.getAbsolutePath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Toast.makeText(this, "Reproduciendo audio...", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al reproducir el audio", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No hay audio para reproducir", Toast.LENGTH_SHORT).show();
            }

    }
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void Guardar() {
        String Inombre = nombre.getText().toString().trim();
        String Itelefono = telefono.getText().toString().trim();
        String Ilatitud = latitud.getText().toString().trim();
        String Ilongitud = longitud.getText().toString().trim();

        // Validar campos
        if (TextUtils.isEmpty(Inombre) || TextUtils.isEmpty(Itelefono) || TextUtils.isEmpty(Ilatitud) || TextUtils.isEmpty(Ilongitud) || audioFile == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error de Validación");
            builder.setMessage("Por favor complete todos los campos.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }


        String audioFileUrl = audioFile.getAbsolutePath().toString();

        servicio.createContactos(Inombre, Itelefono, Ilatitud, Ilongitud, audioFileUrl).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {

                    Toast.makeText(MainActivity.this, response.body().getStatus(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error al guardar el contacto", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
            Toast.makeText(MainActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            Log.e("Error", t.getMessage());}
        });

    }



    private void Permisosaudio() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MICRONEPHONE_REQUEST_CODE);
        } else {
            grabaraudio();
        }
    }

    private void grabaraudio() {
         audioFile = null;
        try {
            audioFile = createAudioFile();
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mediaRecorder.setOutputFile(audioFile.getAbsolutePath());

            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;

            Toast.makeText(this, "Grabando...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
          Toast.makeText(this, "Error al grabar el audio", Toast.LENGTH_SHORT).show();
        }
    }
    private File createAudioFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String audioFileName = "AUD_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        audioFile = File.createTempFile(audioFileName, ".mp3", storageDir);
        return audioFile;
    }


    private void deteneraudio() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                Toast.makeText(this, "Audio grabado exitosamente", Toast.LENGTH_SHORT).show();
            } catch (RuntimeException stopException) {
                Toast.makeText(this, "Error al detener el audio", Toast.LENGTH_SHORT).show();
            } finally {
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;
            }
        }
    }

    //Metodos para obtener la ubicación actual
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    protected void onStop() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            PermisosLocation();
            return;
        }
        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
        fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latitud.setText(Double.toString(location.getLatitude()));
                            longitud.setText(Double.toString(location.getLongitude()));
                        }
                    }
                });
    }


    private void PermisosLocation() {
        ActivityCompat.requestPermissions(this, new String[]
                {android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MICRONEPHONE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                grabaraudio();
            } else {
                Log.e("MainActivity", "Permisos de grabación no concedidos.");
            }
        } else if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onConnected(null);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("MainActivity", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("MainActivity", "onConnectionFailed:" + connectionResult.getErrorCode());
    }
}