package client;

import static android.view.View.*;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pm1e2grupo6.R;
import com.google.android.gms.common.api.Api;

import model.ApiResponse;
import model.Contacto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Adapter extends ArrayAdapter<Contacto> implements Filterable {
    OnActionListener showDialog = null;
    OnActionListener UD = null;
    Context context = null;
    private List<Contacto> originalData;
    private List<Contacto>  filteredData;

    public Adapter(Context context, List<Contacto> items, OnActionListener showDialog, OnActionListener UD) {
        super(context, 0, items);
        this.showDialog = showDialog;
        this.UD = UD;
        this.context = context;
        this.originalData = items;
        this.filteredData = new ArrayList<>(items);
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


                UD.onAction(contacto, context);

            }
        });


        return convertView;

    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Contacto getItem(int position) {
        return filteredData.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.values = originalData;
                    results.count = originalData.size();
                } else {
                    List<Contacto> filterResultsData = new ArrayList<>();
                    for (Contacto data : originalData) {
                        // Aquí defines cómo determinar si un elemento coincide con la búsqueda
                        if (data.getNombre().toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filterResultsData.add(data);
                        }
                    }
                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (List<Contacto>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}

