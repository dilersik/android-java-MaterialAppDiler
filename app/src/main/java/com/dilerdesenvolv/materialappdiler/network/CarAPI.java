package com.dilerdesenvolv.materialappdiler.network;

import com.dilerdesenvolv.materialappdiler.domain.Car;
import com.dilerdesenvolv.materialappdiler.domain.Contact;
import com.dilerdesenvolv.materialappdiler.domain.WrapObjToNetwork;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface CarAPI {

    @GET("package/ctrl/{ctrlCar}")
    Call<Car> getLuxuryCar(@Path("ctrlCar") String ctrl);

    @FormUrlEncoded
    @POST("package/ctrl/CtrlCarRetrofit.php")
    Call<Car> getOneCar(@Field("method") String method);

    @FormUrlEncoded
    @POST("package/ctrl/CtrlCarRetrofit.php")
    Call<List<Car>> getManyCars(@Field("method") String method);

    @FormUrlEncoded
    @POST("package/ctrl/CtrlCarRetrofit.php")
    Call<List<Car>> searchCars(@Field("method") String method, @Field("term") String ter, @Field("car") String carJson);

    @FormUrlEncoded
    @POST("package/ctrl/CtrlCarRetrofit.php")
    Call<List<Car>> listCars(@Field("method") String method, @Field("car") String carJson, @Field("isNewer") boolean isNewer);

    @POST("package/ctrl/CtrlContact.php")
    Call<Contact> sendContact(@Body WrapObjToNetwork wrapRequest);

    @Multipart
    @POST("package/ctrl/CtrlCarRetrofit.php")
    Call<Car> sendImg(@Part("method") String method, @Part("name_image") String imageName, @Part("binary_image") RequestBody requestBody);

    @FormUrlEncoded
    @POST("package/ctrl/CtrlCarRetrofit.php")
    Call<Car> sendHeader(@Header("mega-test") String megaTest, @Field("method") String method);

    @FormUrlEncoded
    @POST("package/ctrl/CtrlCarRetrofit.php")
    Call<Car> saveCars(@Field("method") String method, @Field("cars") String carsJson);

}
