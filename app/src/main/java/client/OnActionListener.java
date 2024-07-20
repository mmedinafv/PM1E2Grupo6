package client;

import android.content.Context;

import model.Contacto;

@FunctionalInterface
public interface OnActionListener {
    void onAction(Contacto contacto, Context context);
}
