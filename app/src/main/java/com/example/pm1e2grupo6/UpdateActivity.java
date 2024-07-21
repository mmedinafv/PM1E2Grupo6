package com.example.pm1e2grupo6;

import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import client.Client;
import client.Servicios;
import model.ApiResponse;
import model.Contacto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int MICRONEPHONE_REQUEST_CODE = 200;

    private GoogleApiClient googleApiClient;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;

    private Button btnReproducir, btnGuardar, btnGrabar, btnUbi;
    private MediaPlayer mediaPlayer;

    private EditText nombre, telefono, latitud, longitud;
    private Servicios servicios;

    private File audioFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        nombre = findViewById(R.id.nombre);
        latitud = findViewById(R.id.latitud);
        longitud = findViewById(R.id.longitud);
        telefono = findViewById(R.id.telefono);
        btnGrabar = findViewById(R.id.btnGrabar);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnReproducir = findViewById(R.id.btnReproducir);
        btnUbi = findViewById(R.id.btnUbi);
        ImageView imageView = findViewById(R.id.imgViewId);

        servicios = new Client().getInstancia().getServicios();

        CargarContacto(9);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateContact();
            }
        });
        btnReproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reproducirAudio();
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
        btnUbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerUbicacion();
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


    private void CargarContacto(int id) {
        Call<ApiResponse> call = servicios.getContactosByID(id);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.getData() != null && apiResponse.getData().getContent() != null) {
                        Contacto contacto = (Contacto) apiResponse.getData().getContent().get(0);
                        getContact(contacto);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(UpdateActivity.this, "Failed to fetch contact details", Toast.LENGTH_SHORT).show();
                Log.e("UpdateActivity", t.getMessage(), t);
            }
        });
    }


    private void getContact(Contacto contacto) {
        nombre.setText(contacto.getNombre());
        telefono.setText(contacto.getTelefono());
        latitud.setText(contacto.getLatitud());
        longitud.setText(contacto.getLongitud());
    }

    private void updateContact() {
        String name = nombre.getText().toString();
        String phone = telefono.getText().toString();
        String lat = latitud.getText().toString();
        String lng = longitud.getText().toString();

        String audioFileUrl = "";
        if (audioFile != null) {
            audioFileUrl = audioFile.getAbsolutePath().toString();
        }

        Call<ApiResponse> call = servicios.updateContactos(name, phone, lat, lng, audioFileUrl,9);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(UpdateActivity.this, "Contacto actualizado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(UpdateActivity.this, "Error al actualizar el contacto", Toast.LENGTH_SHORT).show();
                Log.e("UpdateActivity", t.getMessage(), t);
            }
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

    private void obtenerUbicacion() {
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
    public void onConnected(@Nullable Bundle bundle) {
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
