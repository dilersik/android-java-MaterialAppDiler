package com.dilerdesenvolv.materialappdiler.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dilerdesenvolv.materialappdiler.MainActivity;
import com.dilerdesenvolv.materialappdiler.R;
import com.dilerdesenvolv.materialappdiler.domain.Car;
import com.dilerdesenvolv.materialappdiler.extras.CarDes;
import com.dilerdesenvolv.materialappdiler.extras.DataUrl;
import com.dilerdesenvolv.materialappdiler.network.CarAPI;
import com.dilerdesenvolv.materialappdiler.provider.CarWidgetProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by T-Gamer on 05/08/2016.
 */
public class CarWidgetFactoryAdapter implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private List<Car> mListCar;
    private int mSize;

    public CarWidgetFactoryAdapter(Context context, Intent intent) {
        mContext = context;

        float scale = mContext.getResources().getDisplayMetrics().density;
        mSize = (int) (50 * scale + 0.5f);
    }

    @Override
    public void onCreate() {
        callRetrofit();
    }

    @Override
    public void onDataSetChanged() {
        callRetrofit();
    }

    @Override
    public void onDestroy() { }

    @Override
    public int getCount() {
        return mListCar.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.appwidget_item);
        views.setTextViewText(R.id.tv_model, mListCar.get(position).getModel());
        views.setTextViewText(R.id.tv_brand, mListCar.get(position).getBrand());

        try {
            Bitmap myBitmap = Glide
                    .with(mContext)
                    .load(DataUrl.getUrl(mListCar.get(position).getUrlPhoto(), mSize))
                    .asBitmap()
                    .centerCrop()
                    .into(mSize, mSize)
                    .get();

            views.setImageViewBitmap(R.id.iv_car, myBitmap);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Intent itFilter = new Intent();
        itFilter.putExtra(CarWidgetProvider.FILTER_CAR_ITEM, mListCar.get(position).getId()); // melhor se fosse ID
        views.setOnClickFillInIntent(R.id.rl_container, itFilter);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private void callRetrofit() {
        mListCar = new ArrayList<>();

        Gson gson = new GsonBuilder().registerTypeAdapter(Car.class, new CarDes()).create();
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(Car.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        CarAPI carAPI = retrofit.create(CarAPI.class);
        Car car = new Car();
        final Call<List<Car>> callManyCars = carAPI.listCars("list-cars", new Gson().toJson(car), false);
        callManyCars.enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                if (response != null) {
                    for (Car c : response.body()) {
                        mListCar.add(c);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                Toast.makeText(mContext, R.string.falhou_tente_nova, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
