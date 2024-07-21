package com.example.pm1e2grupo6;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.pm1e2grupo6.databinding.ActivityListBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;

import java.util.List;
import client.Adapter;
import client.Client;
import client.Servicios;
import model.ApiResponse;
import model.Contacto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListActivity extends AppCompatActivity {
    private ActivityListBinding binding;
    public static final int REQUEST_CALL_PHONE = 103;
    public static final int REQUEST_MEDIA_PLAYER = 104;
    public static final int REQUEST_UBICACION = 105;
    public static final int REQUEST_READ = 106;
    private static MediaPlayer mediaPlayer;
    private File audioFile;
    private Adapter adapter;
    private static int id = 0;
    private static Contacto contactoSeleccionado = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityListBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        Servicios service = (new Client()).getServicios();
        service.getContactos().enqueue(
                new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if(response.isSuccessful()){
                            Gson gson = new Gson();
                            String jsonContext = gson.toJson(response.body().getData().getContent());
                            Type type = new TypeToken<List<Contacto>>(){}.getType();
                            List<Contacto> contactos = gson.fromJson(jsonContext, type);

                            adapter = new Adapter(ListActivity.this, contactos, ListActivity::msjConfirmacionLlamada, ListActivity::UD);

                            ListView listView = binding.listaContactos;
                            listView.setAdapter(adapter);
                            Log.d("**** Http ****","success");
                        }
                        Log.d("**** Http ****","no success");
                    }
                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Log.d("**** Http ****","failure");
                        t.printStackTrace();
                    }
                }
        );
        binding.buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.btnEliminar.setOnClickListener(v -> {
            if(id != 0){
                deleteContacto(id, this);
                id=0;
                adapter.remove(contactoSeleccionado);
                contactoSeleccionado = null;
            }
        });
        binding.btnActualizar.setOnClickListener(v -> {
            if(id != 0){
                updateContacto(id, this);
            }
        });

    }

    private static void updateContacto(int id, Context context){
        Intent intent = new Intent(context, UpdateActivity.class);
        intent.putExtra("id", id);
        ((Activity) context).startActivity(intent);
        ((Activity) context).finish();
    }
    private static void UD(Contacto contacto, Context context){
        id = contacto.getId();
        contactoSeleccionado = contacto;
    }

    private static void deleteContacto(int id, Context context){
        Servicios service = (new Client()).getServicios();
        service.deleteContactos(id).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(context, "Contacto Eliminado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d("**** Http ****","failure");
                t.printStackTrace();
            }
        });

    }

    private static void msjConfirmacionLlamada(Contacto contacto, Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Acción");

        builder.setMessage("Que desea hacer con: " + contacto.getNombre() + "?");
        //builder.setPositiveButtonIcon(ContextCompat.getDrawable(context, R.drawable.baseline_phone_10));
        builder.setPositiveButton("Llamar", new DialogInterface.OnClickListener()

        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(checkCallPermission(context, REQUEST_CALL_PHONE)){
                    llamar(context,contacto.getTelefono());
                }

            }
        });
        //builder.setNeutralButtonIcon(ContextCompat.getDrawable(context, R.drawable.baseline_location_on_10));
        builder.setNeutralButton("Ir Ubicación",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent ubicacionIntent = new Intent(context, MapsActivity.class);
                ubicacionIntent.putExtra("id", contacto.getId());
                context.startActivity(ubicacionIntent);
            }
        });

        //builder.setNegativeButtonIcon(ContextCompat.getDrawable(context, R.drawable.baseline_audiotrack_10));
        builder.setNegativeButton("Escuchar Audio", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(checkCallPermission(context, REQUEST_MEDIA_PLAYER)){
                    File audio = new File("files/Music/AUD_20240720_214759_78872893109.mp3");
                    Log.i("URL", audio.getAbsolutePath());
                    reproducirAudio(context,audio );
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CALL_PHONE){
            if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(),"No se proporcionaron permisos para llamar", Toast.LENGTH_LONG);
            }
        }
    }

    public static boolean checkCallPermission(Context context, int requestCode){
        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE},  requestCode);

            if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    public static void llamar(Context context, String phonenumber){
        Intent llamarIntent = new Intent(Intent.ACTION_CALL);
        if(llamarIntent.resolveActivity(context.getPackageManager()) == null){
            llamarIntent.setData(Uri.parse("tel:"+phonenumber));
            ((Activity) context).startActivity(llamarIntent);
        }
    }

    private static boolean reproducirAudio(Context context, File audioFile) {
        if (audioFile == null) { // Corrected the condition by adding not operator
            Toast.makeText(context, "No hay audio para reproducir", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(audioFile.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(context, "Reproduciendo audio...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al reproducir el audio", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public static File getFileStorage(Context context, String path){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ);
            return null;
        }else{
            //File storagge = Environment.getExternalStoragePublicDirectory();
            //File file = new File(storagge, path);
            //if(!file.exists()){
            //    file.mkdirs();
            //    return null;
            //}
            //return file;
        }
        return null;
    }

}
