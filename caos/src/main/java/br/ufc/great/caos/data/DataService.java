package br.ufc.great.caos.data;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface DataService {

    @GET("/api/caos")
    Call<ResponseBody> getDatas();

    @POST("/api/caos")
    Call<ResponseBody> postDatas(@Body RequestBody json);
    
    @POST("/api/caos/filter")
    Call<ResponseBody> filterDatas(@Body RequestBody json);
}