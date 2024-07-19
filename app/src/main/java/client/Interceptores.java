package client;

import okhttp3.Interceptor;
import okhttp3.Request;

public class Interceptores implements Interceptor {
    @Override
    public okhttp3.Response intercept(okhttp3.Interceptor.Chain chain) throws java.io.IOException {
        Request interceptores = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", "Retrofit-Sample-App")
                .addHeader("charset", "UTF-8")
                .build();
        return chain.proceed(interceptores);
    }
}
