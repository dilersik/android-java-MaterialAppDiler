package com.dilerdesenvolv.materialappdiler.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.dilerdesenvolv.materialappdiler.MainActivity;
import com.dilerdesenvolv.materialappdiler.R;
import com.dilerdesenvolv.materialappdiler.adapters.CarAdapter;
import com.dilerdesenvolv.materialappdiler.domain.Car;
import com.dilerdesenvolv.materialappdiler.domain.WrapObjToNetwork;
import com.dilerdesenvolv.materialappdiler.extras.Util;
import com.dilerdesenvolv.materialappdiler.network.NetworkConnection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by T-Gamer on 22/07/2016.
 */
public class CarOldFragment extends CarFragment {

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        if (savedInstanceState != null ){
//            mListCar = savedInstanceState.getParcelableArrayList("mListCar");
//        } else {
//            mListCar = ((MainActivity) getActivity()).getCarsByCategory(3);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car, container, false);

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

                StaggeredGridLayoutManager llm = (StaggeredGridLayoutManager) mRecyclerView.getLayoutManager();
                int[] aux = llm.findLastCompletelyVisibleItemPositions(null);
                int max = -1;
                for (int i = 0; i < aux.length; i ++) {
                    max = aux[i] > max ? aux[i] : max;
                }

                // se estiver no ultimo item, carrega mais
                if (!isLastItem
                        && mListCar.size() == max + 1
                        && (mSwipeRefreshLayout == null || !mSwipeRefreshLayout.isRefreshing())) {

                    callRetrofit(CarOldFragment.class.getName());
//                    NetworkConnection.getInstance(getActivity()).execute(CarOldFragment.this, CarOldFragment.class.getName());
                }
            }
        });
        mRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), mRecyclerView, this));

        StaggeredGridLayoutManager llm = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        llm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mRecyclerView.setLayoutManager(llm);

        CarAdapter adapter = new CarAdapter(getActivity(), mListCar, false, true);
        adapter.setRecyclerViewOnClickListenerHack(this);
        mRecyclerView.setAdapter(adapter);

        setFloatingActionButton(view);

        callRetrofit(CarOldFragment.class.getName());

        // SWIPE REFRESH LAYOUT
        swipeRefreshLayout(view, CarOldFragment.class.getName());
//        swipeRefreshLayout(view, this, CarOldFragment.class.getName());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mListCar = savedInstanceState.getParcelableArrayList("mListCar");

            // HACKCODE TO KEEP THE CORRECT LIST LINKED IN RECYCLERVIEW ADAPTER
            CarAdapter adapter = new CarAdapter(getActivity(), mListCar, false, true);
            mRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
//        NetworkConnection.getInstance(getActivity()).getRequestQueue().cancelAll(CarLuxuryFragment.class.getName());
    }

    // NETWORK
//    @Override
//    public WrapObjToNetwork doBefore() {
//        mPbLoad.setVisibility(mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing() ? View.GONE : View.VISIBLE);
//
//        if (Util.verifyConnection(getActivity())) {
//            Car car = new Car();
//            car.setCategory(3); // muda a porra da categoria
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
//    public void callVolleyRequest() {
//        NetworkConnection.getInstance(getActivity()).execute(this, CarOldFragment.class.getName());
//    }

}
