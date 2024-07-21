package client;


import model.ApiResponse;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface Servicios {
    @GET("/")
    Call<ApiResponse> getContactos();

    @GET("/")
    Call<ApiResponse> getContactosByID(@Query("id") int id);

    @FormUrlEncoded
    @POST("/")
    Call<ApiResponse> createContactos(@Field("nombre") String nombre,
                                      @Field("telefono") String telefono,
                                      @Field("latitud") String latitud,
                                      @Field("longitud") String longitud,
                                      @Field("URI") String URL);

    @FormUrlEncoded
    @PUT("/")
    Call<ApiResponse> updateContactos(
            @Field("nombre") String nombre,
            @Field("telefono") String telefono,
            @Field("latitud") String latitud,
            @Field("longitud") String longitud,
            @Field("URI") String URL,
            @Field("id") String id
    );

    @DELETE("/")
    Call<ApiResponse> deleteContactos(
            @Query("id") int id
    );
}
