package client;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.TextView;

import java.util.List;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.pm1e2grupo6.R;
import com.google.android.gms.common.api.Api;

import model.ApiResponse;
import model.Contacto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class Adapter extends ArrayAdapter<Contacto> {
    OnActionListener showDialog = null;
    Context context = null;
    public Adapter(Context context, List<Contacto> items, OnActionListener showDialog) {
        super(context, 0, items);
        this.showDialog = showDialog;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contacto contacto = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.vista_contacto, parent, false);
        }

        TextView textViewItem = convertView.findViewById(R.id.textView);

        if (contacto != null) {
            textViewItem.setText(contacto.getNombre());
        }

        convertView.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {
                Log.d("Long click", "");
                showDialog.onAction(contacto, context);
                return false;
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String elementoSeleccionado = contacto.getNombre();
                Toast.makeText(context, "Ha seleccionado un contacto: " + elementoSeleccionado, Toast.LENGTH_SHORT).show();

            }
        });


        return convertView;

    }
}