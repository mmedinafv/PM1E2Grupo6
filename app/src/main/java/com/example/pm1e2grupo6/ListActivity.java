package com.example.pm1e2grupo6;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pm1e2grupo6.databinding.ActivityListBinding;

import java.util.ArrayList;
import java.util.List;

import client.Adapter;
import client.Client;
import client.Servicios;
import model.ApiResponse;
import model.Contacto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class ListActivity extends AppCompatActivity {

    private ActivityListBinding binding;

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

        List<Contacto> listacontacto = new ArrayList<>();
        listacontacto.add(new Contacto(7,"a","44444444","14.3080939","-88.1700663","/storage/emulated/0/Android/data/com.example.pm1e2grupo6/files/Music/AUD_20240719_175227_39385982764"));
        listacontacto.add(new Contacto(8,"a","44444444","14.3080939","-88.1700663","/storage/emulated/0/Android/data/com.example.pm1e2grupo6/files/Music/AUD_20240719_175227_39385982764"));
        listacontacto.add(new Contacto(9,"ignacio","25252525","14.3081114","-88.170088","content://com.example.pm1e2grupo6.fileprovider/my_music/AUD_20240719_181515_1309646099200939644.mp3"));
        listacontacto.add(new Contacto(10,"ignacio","25252525","14.3081114","-88.170088","content://com.example.pm1e2grupo6.fileprovider/my_music/AUD_20240719_181515_1309646099200939644.mp3"));
        listacontacto.add(new Contacto( 11,"aaaa","25252525","14.3080968","-88.1700659","content://com.example.pm1e2grupo6.fileprovider/my_music/AUD_20240719_181702_3222320494374645639.mp3"));
        listacontacto.add(new Contacto( 12,"66yyhy","6655662","66666","777777","content://com.example.pm1e2grupo6.fileprovider/my_music/AUD_20240719_184421_4406806022095811767.mp3"));
        listacontacto.add(new Contacto(13,"aaaa","aaaa","aaaa","aaaa","content://com.example.pm1e2grupo6.fileprovider/my_music/AUD_20240719_193600_2581823401267694420.mp3"));


        Servicios service = (new Client()).getServicios();

        service.getContactos().enqueue(
                new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if(response.isSuccessful()){
                            Adapter adapter = new Adapter(ListActivity.this, response.body().getData().getContent());
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

       // Adapter adapter = new Adapter(this, listacontacto);

      //  ListView listView = binding.listaContactos;
       // listView.setAdapter(adapter);



        /*
        contactosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                contactop = position;
                String elementoSeleccionado = (String) parent.getItemAtPosition(position);

                Toast.makeText(getApplicationContext(), "Ha seleccionado un contacto", Toast.LENGTH_SHORT).show();
                Contacto contactoSeleccionado = lista.get(position);
            }
        });
        contactosList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                contactoSeleccionado = lista.get(position);
                msjConfirmacionLlamada(contactoSeleccionado);
                return true;
            }
        });

        Button btnBack = findViewById(R.id.btnAtras);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("id", 25);
        startActivity(intent);
    }

    private void obtenerInfo(){

        Contacto contacto = null;
        lista = new ArrayList<Contacto>();

        //cursor para recorrer los datos de la tabla
      //  Cursor cursor = db.rawQuery(Trans.SelectAllContactos, null);

      /*  while(cursor.moveToNext())
        {
            contacto = new Contacto();
            contacto.setId(cursor.getInt(0));
            contacto.setNombre(cursor.getString(1));
            contacto.setTelefono(cursor.getString(2));
            contacto.setLatitud(cursor.getString(3));
            contacto.setLongitud(cursor.getString(4));

            lista.add(contacto);
        }
        cursor.close();
        FillDate();*/

    }


    private void msjConfirmacionLlamada(Contacto contacto){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Acción");
        builder.setMessage("¿Desea reproducir el audio de " + contacto.getNombre() + "?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // realizarLlamada(contacto);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
