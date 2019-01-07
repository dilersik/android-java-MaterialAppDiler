package com.dilerdesenvolv.materialappdiler;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.SearchRecentSuggestions;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dilerdesenvolv.materialappdiler.adapters.CarAdapter;
import com.dilerdesenvolv.materialappdiler.domain.Car;
import com.dilerdesenvolv.materialappdiler.domain.WrapObjToNetwork;
import com.dilerdesenvolv.materialappdiler.extras.CarDes;
import com.dilerdesenvolv.materialappdiler.extras.Util;
import com.dilerdesenvolv.materialappdiler.interfaces.RecyclerViewOnClickListenerHack;
import com.dilerdesenvolv.materialappdiler.network.CarAPI;
import com.dilerdesenvolv.materialappdiler.network.NetworkConnection;
import com.dilerdesenvolv.materialappdiler.network.Transaction;
import com.dilerdesenvolv.materialappdiler.provider.SearchableProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchableActivity extends AppCompatActivity implements RecyclerViewOnClickListenerHack {

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private List<Car> mListCar;
    private CarAdapter mCarAdapter;
    private CoordinatorLayout mClcontainer;
    private ProgressBar mPbLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            mListCar = savedInstanceState.getParcelableArrayList("mListCar");
        } else {
            mListCar = new ArrayList<>();
        }

        mClcontainer = (CoordinatorLayout) findViewById(R.id.cl_container);

        mPbLoad = (ProgressBar) findViewById(R.id.pb_load);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager( this );
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        mCarAdapter = new CarAdapter(this, mListCar);
        mCarAdapter.setRecyclerViewOnClickListenerHack(this);
        mRecyclerView.setAdapter(mCarAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(this, mRecyclerView, this));

        hendleSearch(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        hendleSearch(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("mListCar", (ArrayList<Car>) mListCar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_searchable_activity, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView;
        MenuItem item = menu.findItem(R.id.action_searchable_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            searchView = (SearchView) item.getActionView();
        } else {
            searchView = (SearchView) MenuItemCompat.getActionView(item);
        }

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getResources().getString(R.string.buscar_carros));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_delete) {
            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this, SearchableProvider.AUTHORITY, SearchableProvider.MODE);
            searchRecentSuggestions.clearHistory();

            Toast.makeText(SearchableActivity.this, R.string.historico_exc, Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    @Override
    public void onClickListener(View view, int position) {
        Intent intent = new Intent(this, CarActivity.class);
        intent.putExtra("mCar", mListCar.get(position));

        // TRANSITIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View ivCar = view.findViewById(R.id.iv_car);
            View tvModel = view.findViewById(R.id.tv_model);
            View tvBrand = view.findViewById(R.id.tv_brand);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                    Pair.create(ivCar, "element1"),
                    Pair.create( tvModel, "element2" ),
                    Pair.create( tvBrand, "element3" ));

            startActivity(intent, options.toBundle() );
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onLongPressClickListener(View view, int position) {
        Toast.makeText(this, "onLongPressClickListener position: " + position, Toast.LENGTH_SHORT).show();
    }

    // NETWORK
//    @Override
//    public WrapObjToNetwork doBefore() {
//        mPbLoad.setVisibility(View.VISIBLE);
//
//        if (Util.verifyConnection(this)) {
//            Car car = new Car();
//            car.setCategory(0);
//
//            if (mListCar != null && mListCar.size() > 0) {
//                car.setId(mListCar.get(mListCar.size() - 1).getId());
//            }
//
//            return new WrapObjToNetwork(car, "get-cars-search", mToolbar.getTitle().toString());
//        }
//
//        return null;
//    }
//
//    @Override
//    public void doAfter(JSONArray jsonArray) {
//        mPbLoad.setVisibility(View.GONE);
//
//        if (jsonArray != null) {
//            CarAdapter adapter = (CarAdapter) mRecyclerView.getAdapter();
//            Gson gson = new Gson();
//
//            try {
//                for (int i = 0, tamI = jsonArray.length(); i < tamI; i++) {
//                    Car car = gson.fromJson(jsonArray.getJSONObject(i).toString(), Car.class);
//                    adapter.addListItem(car, mListCar.size());
//                }
//            } catch (JSONException e) {
//                Log.i("LOG", "erro doAfter(): " + e.getMessage());
//            }
//        } else {
//            Toast.makeText(this, R.string.falhou_tente_nova, Toast.LENGTH_SHORT).show();
//        }
//
//        mRecyclerView.setVisibility(mListCar.isEmpty() ? View.GONE : View.VISIBLE);
//        if (mListCar.isEmpty()) {
//            TextView tv = new TextView(this);
//            tv.setText(getResources().getString(R.string.nenhum_result));
//            tv.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimarytext));
//            tv.setId(1); // pode gambis
//            tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
//            tv.setGravity(Gravity.CENTER);
//
//            mClcontainer.addView(tv);
//        } else if (mClcontainer.findViewById(1) != null) {
//            mClcontainer.removeView(mClcontainer.findViewById(1));
//        }
//    }

    protected static class RecyclerViewTouchListener implements RecyclerView.OnItemTouchListener {
        private Context mContext;
        private GestureDetector mGestureDetector;
        private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;

        public RecyclerViewTouchListener(Context c, final RecyclerView rv, RecyclerViewOnClickListenerHack rvoclh) {
            mContext = c;
            mRecyclerViewOnClickListenerHack = rvoclh;

            mGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);

                    View cv = rv.findChildViewUnder(e.getX(), e.getY());
                    if (cv != null && mRecyclerViewOnClickListenerHack != null) {
                        mRecyclerViewOnClickListenerHack.onLongPressClickListener(cv, rv.getChildAdapterPosition(cv));
                    }
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    View cv = rv.findChildViewUnder(e.getX(), e.getY());

                    boolean callContextMenuStatus = false;
                    if (cv instanceof CardView) {
                        float x = ((RelativeLayout) ((CardView) cv).getChildAt(0)).getChildAt(3).getX();
                        float w = ((RelativeLayout) ((CardView) cv).getChildAt(0)).getChildAt(3).getWidth();
                        float y; // pega posicao estatica = ((RelativeLayout) ((CardView) cv).getChildAt(0)).getChildAt(3).getY();
                        float h = ((RelativeLayout) ((CardView) cv).getChildAt(0)).getChildAt(3).getHeight();

                        Rect rect = new Rect();
                        ((RelativeLayout) ((CardView) cv).getChildAt(0)).getChildAt(3).getGlobalVisibleRect(rect);
                        y = rect.top;

                        if (e.getX() >= x && e.getX() <= w + x &&
                                e.getRawY() >= y && e.getRawY() <= h + y) {
                            callContextMenuStatus = true;
                        }
                    }

                    if (cv != null && mRecyclerViewOnClickListenerHack != null && !callContextMenuStatus) {
                        mRecyclerViewOnClickListenerHack.onClickListener(cv, rv.getChildAdapterPosition(cv));
                    }

                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            mGestureDetector.onTouchEvent(e);

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) { }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) { }

    }

    public void hendleSearch(Intent intent) {
        if (Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())) {
            String q = intent.getStringExtra(SearchManager.QUERY);

            mToolbar.setTitle(q);
            filterCars(q);

            SearchRecentSuggestions searchRecentSuggestions = new SearchRecentSuggestions(this, SearchableProvider.AUTHORITY, SearchableProvider.MODE);
            searchRecentSuggestions.saveRecentQuery(q, null);
        }
    }

    public void filterCars(String q) {
        if (!Util.verifyConnection(this)) {
            android.support.design.widget.Snackbar.make(findViewById(R.id.cl_container), R.string.sem_conexao_verifique, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.OK, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivity(it);
                        }
                    })
                    .setActionTextColor(ContextCompat.getColor(this, R.color.colorLink))
                    .show();
            return;
        }

        mListCar.clear();
        mPbLoad.setVisibility(View.VISIBLE);

        Car car = new Car();
        car.setCategory(0);

        if (mListCar != null && mListCar.size() > 0) {
            car.setId(mListCar.get(mListCar.size() - 1).getId());
        }

        Gson gson = new GsonBuilder().registerTypeAdapter(Car.class, new CarDes()).create();
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(Car.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        CarAPI carAPI = retrofit.create(CarAPI.class);
        final Call<List<Car>> callManyCars = carAPI.searchCars("search-cars", q, new Gson().toJson(car));
        final TextView tv = new TextView(this);
        new Thread(){
            @Override
            public void run() {
                super.run();

                try {
                    List<Car> listCars = callManyCars.execute().body();
                    if (listCars != null) {
                        for (Car c : listCars) {
                            //adapter.addListItem(c, mListCar.size());
                            mListCar.add(c);
                            Log.i("LOG", "Car: " + c.getModel());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    android.support.design.widget.Snackbar.make(findViewById(R.id.cl_container),
                            R.string.falhou_tente_nova,
                            Snackbar.LENGTH_LONG)
                            .show();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("LOG", "MANY CAR request Ok");
                        mPbLoad.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(mListCar.isEmpty() ? View.GONE : View.VISIBLE);

                        if (mListCar.isEmpty()) {
                            tv.setText(getResources().getString(R.string.nenhum_result));
                            tv.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimarytext));
                            tv.setId(1); // pode gambis
                            tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                            tv.setGravity(Gravity.CENTER);

                            mClcontainer.addView(tv);
                        } else if (mClcontainer.findViewById(1) != null) {
                            mClcontainer.removeView(mClcontainer.findViewById(1));
                        }

                        mCarAdapter.notifyDataSetChanged();
                    }
                });
            }
        }.start();



        //NetworkConnection.getInstance(this).execute(this, SearchableActivity.class.getName());

        // BUSCA FAKE NO ARRAY
//        for (int i = 0, tamI = mListCar.size(); i < tamI; i ++) {
//            if (mListCar.get(i).getModel().toLowerCase().startsWith(q.toLowerCase())) {
//                mListAuxCar.add(mListCar.get(i));
//            }
//        }
//        for (int i = 0, tamI = mListCar.size(); i < tamI; i ++) {
//            if (!mListAuxCar.contains(mListCar.get(i)) && mListCar.get(i).getBrand().toLowerCase().startsWith(q.toLowerCase())) {
//                mListAuxCar.add(mListCar.get(i));
//            }
//        }
//
//        mRecyclerView.setVisibility(mListAuxCar.isEmpty() ? View.GONE : View.VISIBLE);
//        if (mListAuxCar.isEmpty()) {
//            TextView tv = new TextView(this);
//            tv.setText(getResources().getString(R.string.nenhum_result));
//            tv.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimarytext));
//            tv.setId(1); // pode gambis
//            tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
//            tv.setGravity(Gravity.CENTER);
//
//            mClcontainer.addView(tv);
//        } else if (mClcontainer.findViewById(1) != null) {
//            mClcontainer.removeView(mClcontainer.findViewById(1));
//        }
//
//        mCarAdapter.notifyDataSetChanged();
    }

}
