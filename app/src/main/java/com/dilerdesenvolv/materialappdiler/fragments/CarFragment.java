package com.dilerdesenvolv.materialappdiler.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dilerdesenvolv.materialappdiler.CarActivity;
import com.dilerdesenvolv.materialappdiler.R;
import com.dilerdesenvolv.materialappdiler.adapters.CarAdapter;
import com.dilerdesenvolv.materialappdiler.domain.Car;
import com.dilerdesenvolv.materialappdiler.extras.CarDes;
import com.dilerdesenvolv.materialappdiler.extras.Util;
import com.dilerdesenvolv.materialappdiler.interfaces.RecyclerViewOnClickListenerHack;
import com.dilerdesenvolv.materialappdiler.network.CarAPI;
import com.dilerdesenvolv.materialappdiler.network.NetworkConnection;
//import com.melnykov.fab.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CarFragment extends Fragment implements RecyclerViewOnClickListenerHack, View.OnClickListener {

    protected static final String TAG = "LOG";
    protected RecyclerView mRecyclerView;
    protected List<Car> mListCar;
    // android.support.design.widget FAB nativo
    protected android.support.design.widget.FloatingActionButton mFab;
    // https://github.com/Clans/FloatingActionButton
//    protected FloatingActionMenu mFab;
    // http://github.com/shell-software/fab
//    private ActionButton mFab;
    // https://github.com/makovkastar/FloatingActionButton
//    private FloatingActionButton mFab;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected Activity mActivity;
    protected ProgressBar mPbLoad;
    protected boolean isLastItem;

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        if (savedInstanceState != null) {
//            mListCar = savedInstanceState.getParcelableArrayList("mListCar");
//        } else {
//            mListCar = ((MainActivity) getActivity()).getCarsByCategory(0);
//        }
//    }

    @Override
    @Subscribe
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            Log.i("CarFragment onAttach() ", e.getMessage());
        }

        mActivity = context instanceof Activity ? (Activity) context : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_car, container, false);

        mListCar = new ArrayList<>();
        mPbLoad = (ProgressBar) view.findViewById(R.id.pb_load);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // rolando p baixo
                if (dy > 0) {
                    mFab.hide();
//                    mFab.hideMenuButton(true);
//                    mFab.hide();
                } else {
                    mFab.show();
//                    mFab.showMenuButton(true);
//                    mFab.show();
                }

                LinearLayoutManager llm = (LinearLayoutManager) mRecyclerView.getLayoutManager();
//                GridLayoutManager llm = (GridLayoutManager) mRecyclerView.getLayoutManager();
//                StaggeredGridLayoutManager llm = (StaggeredGridLayoutManager) mRecyclerView.getLayoutManager();
//                int[] aux = llm.findLastCompletelyVisibleItemPositions(null);
//                int max = -1;
//                for (int i = 0; i < aux.length; i ++) {
//                    max = aux[i] > max ? aux[i] : max;
//                }

//                CarAdapter adapter = (CarAdapter) mRecyclerView.getAdapter();

                // se estiver no ultimo item, carrega mais
                if (!isLastItem
                        && mListCar.size() == llm.findLastCompletelyVisibleItemPosition() + 1
                        && (mSwipeRefreshLayout == null || !mSwipeRefreshLayout.isRefreshing())) {

                    callRetrofit(CarFragment.class.getName());
//                    NetworkConnection.getInstance(getActivity()).execute(CarFragment.this, CarFragment.class.getName());
                }
            }
        });
        mRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), mRecyclerView, this));

        // Linear layout padrao lista
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        // Layout grid
//        GridLayoutManager llm = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
//        StaggeredGridLayoutManager llm = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
//        llm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        mRecyclerView.setLayoutManager(llm);

//        mListCar = ((MainActivity) getActivity()).getSetCarList(10);
        CarAdapter adapter = new CarAdapter(getActivity(), mListCar);
//        adapter.setRecyclerViewOnClickListenerHack(this);
        mRecyclerView.setAdapter(adapter);

        setFloatingActionButton(view);

        callRetrofit(CarFragment.class.getName());

        // SWIPE REFRESH LAYOUT
        swipeRefreshLayout(view, CarFragment.class.getName());
//        swipeRefreshLayout(view, this, CarFragment.class.getName());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mListCar = savedInstanceState.getParcelableArrayList("mListCar");

            // HACKCODE TO KEEP THE CORRECT LIST LINKED IN RECYCLERVIEW ADAPTER
            CarAdapter adapter = new CarAdapter(getActivity(), mListCar);
            mRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onClickListener(View view, int position) {
        Intent intent = new Intent(getActivity(), CarActivity.class);
        intent.putExtra("mCar", mListCar.get(position));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View ivCar = view.findViewById(R.id.iv_car);
            View tvModel = view.findViewById(R.id.tv_model);
            View tvBrand = view.findViewById(R.id.tv_brand);

            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                    Pair.create(ivCar, "element1"),
                    Pair.create(tvModel, "element2"),
                    Pair.create(tvBrand, "element3"));
//            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), null);
            getActivity().startActivity(intent, optionsCompat.toBundle());
        } else {
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void onLongPressClickListener(View view, int position) {
        Toast.makeText(getActivity(), "onLongPressClickListener position: " + position, Toast.LENGTH_SHORT).show();
//        CarAdapter adapter = (CarAdapter) mRecyclerView.getAdapter();
//        adapter.removeListItem(position);
    }

    @Override
    public void onClick(View v) {
        // https://github.com/Clans/FloatingActionButton
//        String aux = "";
//        switch (v.getId()) {
//            case R.id.fab1:
//                aux = "Fab 1 clicked";
//                break;
//            case R.id.fab2:
//                aux = "Fab 2 clicked";
//                break;
//            case R.id.fab3:
//                aux = "Fab 3 clicked";
//                break;
//            case R.id.fab4:
//                aux = "Fab 4 clicked";
//                break;
//            case R.id.fab5:
//                aux = "Fab 5 clicked";
//                break;
//        }
//
//        Toast.makeText(getActivity(), aux, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("mListCar", (ArrayList<Car>) mListCar);
    }

    @Override
    public void onStop() {
        super.onStop();
//        NetworkConnection.getInstance(getActivity()).getRequestQueue().cancelAll(CarFragment.class.getName());
    }

    public void onEvent(Car car) {
        for (int i = 0; i < mListCar.size(); i ++) {
            if (mListCar.get(i).getUrlPhoto().equalsIgnoreCase(car.getUrlPhoto())  && this.getClass().getName().equalsIgnoreCase(CarFragment.class.getName())) {
                Intent it = new Intent(mActivity, CarActivity.class);
                it.putExtra("mCar", mListCar.get(i));
                mActivity.startActivity(it);

                break;
            }
        }
    }

    protected void callRetrofit(String tag) {
        if (!Util.verifyConnection(getActivity())) {
            android.support.design.widget.Snackbar.make(getActivity().findViewById(R.id.cl_container), R.string.sem_conexao_verifique, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.OK, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivity(it);
                        }
                    })
                    .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.colorLink))
                    .show();
            return;
        }

        mPbLoad.setVisibility(mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing() ? View.GONE : View.VISIBLE);

        Car car = new Car();
        switch (tag) {
            case "com.dilerdesenvolv.materialappdiler.fragments.CarLuxuryFragment":
                car.setCategory(1);
                break;

            case "com.dilerdesenvolv.materialappdiler.fragments.CarSportFragment":
                car.setCategory(2);
                break;

            case "com.dilerdesenvolv.materialappdiler.fragments.CarOldFragment":
                car.setCategory(3);
                break;

            case "com.dilerdesenvolv.materialappdiler.fragments.CarPopularFragment":
                car.setCategory(4);
                break;

            default:
                car.setCategory(0);
                break;
        }

        if (mListCar != null && mListCar.size() > 0) {
            car.setId(mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing() ? mListCar.get(0).getId() :
                    mListCar.get(mListCar.size() - 1).getId());
        }

        Gson gson = new GsonBuilder().registerTypeAdapter(Car.class, new CarDes()).create();
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(Car.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        CarAPI carAPI = retrofit.create(CarAPI.class);
        final Call<List<Car>> callManyCars = carAPI.listCars("list-cars", new Gson().toJson(car), mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing());
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    List<Car> listCars = callManyCars.execute().body();

                    if (listCars != null) {
                        CarAdapter adapter = (CarAdapter) mRecyclerView.getAdapter();
                        int auxPosition = 0, position;

                        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
                            auxPosition = 1;
                        }

                        for (int i = 0, tamI = listCars.size(); i < tamI; i++) {
                            Log.i(TAG, "CarCall: " + listCars.get(i).getModel());

                            position = auxPosition == 0 ? mListCar.size() : 0;
                            adapter.addListItem(listCars.get(i), position);

                            if (auxPosition == 1) {
                                mRecyclerView.getLayoutManager().smoothScrollToPosition(mRecyclerView, null, position);
                            }
                        }

                        if (listCars.size() == 0 && auxPosition == 0 ){
                            isLastItem = true;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (mActivity != null) {
                        android.support.design.widget.Snackbar.make(mActivity.findViewById(R.id.cl_container),
                                R.string.falhou_tente_nova,
                                Snackbar.LENGTH_LONG)
                                .show();
                    }
                }

                if (mActivity != null) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPbLoad.setVisibility(View.GONE);
                            if (mSwipeRefreshLayout != null) {
                                if (mSwipeRefreshLayout.isRefreshing()) {
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        }
                    });
                }
            }
        }.start();
    }

    // NETWORK 2
//    @Override
//    public WrapObjToNetwork doBefore() {
//        mPbLoad.setVisibility(mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing() ? View.GONE : View.VISIBLE);
//
//        if (Util.verifyConnection(getActivity())) {
//            Car car = new Car();
//            car.setCategory(0);
//
//            if (mListCar != null && mListCar.size() > 0) {
//                car.setId(mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing() ? mListCar.get(0).getId() :
//                        mListCar.get(mListCar.size() - 1).getId());
//            }
//
//            return new WrapObjToNetwork(car, "get-cars", (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()));
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
//            int auxPosition = 0, position;
//
//            if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
//                mSwipeRefreshLayout.setRefreshing(false);
//                auxPosition = 1;
//            }
//
//            try {
//                for (int i = 0, tamI = jsonArray.length(); i < tamI; i++) {
//                    Car car = gson.fromJson(jsonArray.getJSONObject(i).toString(), Car.class);
//                    position = auxPosition == 0 ? mListCar.size() : 0;
//
//                    adapter.addListItem(car, position);
//
//                    if (auxPosition == 1) {
//                        mRecyclerView.getLayoutManager().smoothScrollToPosition(mRecyclerView, null, position);
//                    }
//                }
//            } catch (JSONException e) {
//                Log.i(TAG, "erro doAfter(): " + e.getMessage());
//            }
//        } else {
//            Toast.makeText(getActivity(), R.string.falhou_tente_nova, Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    public void callVolleyRequest() {
//        NetworkConnection.getInstance(getActivity()).execute(this, CarFragment.class.getName());
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

    protected void setFloatingActionButton(View view) {
        // android.support.design.widget FAB nativo
        mFab = (android.support.design.widget.FloatingActionButton) getActivity().findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.design.widget.Snackbar.make(v, "FAB clicked", android.support.design.widget.Snackbar.LENGTH_SHORT)
                        .setAction(R.string.OK, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .setActionTextColor(ContextCompat.getColor(getContext(), R.color.colorLink))
                        .show();
            }
        });

        // https://github.com/Clans/FloatingActionButton
//        mFab = (FloatingActionMenu) getActivity().findViewById(R.id.fab);
//        mFab = (FloatingActionMenu) view.findViewById(R.id.fab);
//        mFab.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
//            @Override
//            public void onMenuToggle(boolean opened) {
////                Toast.makeText(getActivity(), "Is menu opened? " + (opened ? "true" : "false"), Toast.LENGTH_SHORT).show();
//            }
//        });
//        mFab.showMenuButton(true);
//        mFab.setClosedOnTouchOutside(true);
//
////        FloatingActionButton fab1 = (FloatingActionButton) getActivity().findViewById(R.id.fab1);
////        FloatingActionButton fab2 = (FloatingActionButton) getActivity().findViewById(R.id.fab2);
////        FloatingActionButton fab3 = (FloatingActionButton) getActivity().findViewById(R.id.fab3);
////        FloatingActionButton fab4 = (FloatingActionButton) getActivity().findViewById(R.id.fab4);
////        FloatingActionButton fab5 = (FloatingActionButton) getActivity().findViewById(R.id.fab5);
//        FloatingActionButton fab1 = (FloatingActionButton) view.findViewById(R.id.fab1);
//        FloatingActionButton fab2 = (FloatingActionButton) view.findViewById(R.id.fab2);
//        FloatingActionButton fab3 = (FloatingActionButton) view.findViewById(R.id.fab3);
//        FloatingActionButton fab4 = (FloatingActionButton) view.findViewById(R.id.fab4);
//        FloatingActionButton fab5 = (FloatingActionButton) view.findViewById(R.id.fab5);
//        fab1.setOnClickListener(this);
//        fab2.setOnClickListener(this);
//        fab3.setOnClickListener(this);
//        fab4.setOnClickListener(this);
//        fab5.setOnClickListener(this);

        // http://github.com/shell-software/fab
//        mFab = (ActionButton) getActivity().findViewById(R.id.fab);
//        mFab.playShowAnimation();
//        mFab.setShowAnimation(ActionButton.Animations.SCALE_UP);
//        mFab.setHideAnimation(ActionButton.Animations.SCALE_DOWN);
//        mFab.setButtonColor(R.color.colorFAB);
//        mFab.setButtonColorPressed(R.color.colorFABPressed);
//        mFab.setImageResource(R.drawable.ic_plus);
//        float scale = getActivity().getResources().getDisplayMetrics().density;
//        int shadow = (int) (3 * scale + 0.5);
//        mFab.setShadowRadius(shadow);
//        mFab.setOnClickListener(this);

        // https://github.com/makovkastar/FloatingActionButton
//        mFab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
//        mFab.attachToRecyclerView(mRecyclerView, new ScrollDirectionListener() {
//            @Override
//            public void onScrollDown() {
//
//            }
//
//            @Override
//            public void onScrollUp() {
//
//            }
//        }, new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                LinearLayoutManager llm = (LinearLayoutManager) mRecyclerView.getLayoutManager();
////                GridLayoutManager llm = (GridLayoutManager) mRecyclerView.getLayoutManager();
////                StaggeredGridLayoutManager llm = (StaggeredGridLayoutManager) mRecyclerView.getLayoutManager();
////                int[] aux = llm.findLastCompletelyVisibleItemPositions(null);
////                int max = -1;
////                for (int i = 0; i < aux.length; i ++) {
////                    max = aux[i] > max ? aux[i] : max;
////                }
//
//                CarAdapter adapter = (CarAdapter) mRecyclerView.getAdapter();
//
//                // se estiver no ultimo item, carrega mais
//                if (mListCar.size() == llm.findLastVisibleItemPosition() + 1) {
////                if (mListCar.size() == max + 1) {
//                    List<Car> listAux = ((MainActivity) getActivity()).getSetCarList(10);
//
//                    for (int i = 0; i < listAux.size(); i ++) {
//                        adapter.addListItem(listAux.get(i), mListCar.size());
//                    }
//                }
//            }
//        });
//        mFab.setOnClickListener(this);
    }

    protected void swipeRefreshLayout(final View view, final String tag) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_swipe);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Util.verifyConnection(getActivity())) {
                    callRetrofit(tag);
//                    NetworkConnection.getInstance(getActivity()).execute(transaction, tag);

                } else {
                    mSwipeRefreshLayout.setRefreshing(false);

                    android.support.design.widget.Snackbar.make(view, R.string.sem_conexao_verifique, android.support.design.widget.Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.OK, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent it = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                    startActivity(it);
                                }
                            })
                            .setActionTextColor(ContextCompat.getColor(getContext(), R.color.colorLink))
                            .show();

                    // com.nispok.Snakebar
//                    SnackbarManager.show(
//                            Snackbar.with(getActivity())
//                                    .type(SnackbarType.MULTI_LINE)
//                                    .text(R.string.sem_conexao_verifique)
//                                    .color(ContextCompat.getColor(getContext(), R.color.black))
//                                    .textColor(ContextCompat.getColor(getContext(), R.color.white))
//                                    .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
//                                    .actionLabel(R.string.conectar)
//                                    .actionColor(ContextCompat.getColor(getContext(), R.color.colorLink))
//                                    .actionListener(new ActionClickListener() {
//                                        @Override
//                                        public void onActionClicked(Snackbar snackbar) {
//                                            Intent it = new Intent(Settings.ACTION_WIFI_SETTINGS);
//                                            startActivity(it);
//                                        }
//                                    })
//                                    .eventListener(new EventListenerAdapter() {
//                                        @Override
//                                        public void onShown(Snackbar snackbar) {
//                                            super.onShown(snackbar);
//                                            ObjectAnimator.ofFloat(mFab, "translationY", - snackbar.getHeight()).start();
//                                        }
//
//                                        @Override
//                                        public void onDismissed(Snackbar snackbar) {
//                                            super.onDismissed(snackbar);
//                                            ObjectAnimator.ofFloat(mFab, "translationY", 0).start();
//                                        }
//                                    }), (ViewGroup) view
//                    );
                }
            }
        });
    }

}
