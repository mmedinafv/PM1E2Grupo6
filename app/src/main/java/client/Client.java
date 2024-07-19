package client;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    private Retrofit retrofit;
    private static Client instance;

    public Client getInstancia(){
        if(instance == null){
            instance = new Client();
        }
        return instance;
    }

    private Retrofit getClient(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient client = new OkHttpClient().newBuilder()
                .addInterceptor(new Interceptores())
                .addInterceptor(interceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        this.retrofit = new Retrofit.Builder()
                .baseUrl("https://examen2api.stcentralhn.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return this.retrofit;
    }

    public Servicios getServicios(){
        this.retrofit = this.getClient();
        return this.retrofit.create(Servicios.class);
    }
}
