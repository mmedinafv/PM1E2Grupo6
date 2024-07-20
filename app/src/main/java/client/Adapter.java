package client;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import android.widget.ArrayAdapter;

import com.example.pm1e2grupo6.R;
import com.google.android.gms.common.api.Api;

import model.ApiResponse;
import model.Contacto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Adapter extends ArrayAdapter<Contacto> {

    public Adapter(Context context, List<Contacto> items) {
        super(context, 0, items);
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

        return convertView;
    }
}