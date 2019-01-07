package com.dilerdesenvolv.materialappdiler.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dilerdesenvolv.materialappdiler.CarActivity;
import com.dilerdesenvolv.materialappdiler.ContactActivity;
import com.dilerdesenvolv.materialappdiler.R;
import com.dilerdesenvolv.materialappdiler.domain.Car;
import com.dilerdesenvolv.materialappdiler.domain.ContextMenuItem;
import com.dilerdesenvolv.materialappdiler.extras.DataUrl;
import com.dilerdesenvolv.materialappdiler.interfaces.RecyclerViewOnClickListenerHack;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by T-Gamer on 15/07/2016.
 */
public class CarAdapter extends RecyclerView.Adapter<CarAdapter.MyViewHolder> {

    private Context mContext;
    private List<Car> mListCar;
    private LayoutInflater mLayoutInflater;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;
    private float mScale;
    private int mWidth, mHeight, mRoundPixels;
    private boolean mWithAnimation, mWithCardLayout;

    public CarAdapter(Context c, List<Car> l){
        this(c, l, true, true);
    }

    public CarAdapter(Context c, List<Car> l, boolean wa, boolean wcl){
        mContext = c;
        mListCar = l;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mWithAnimation = wa;
        mWithCardLayout = wcl;

        mScale = mContext.getResources().getDisplayMetrics().density;
        mWidth = mContext.getResources().getDisplayMetrics().widthPixels - (int)(14 * mScale + 0.5f);
        mHeight = (mWidth / 16) * 9;

        mRoundPixels = (int)(2 * mScale + 0.5f);
    }

    @Override
    // viewGroup é o recycler
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;

        if (mWithCardLayout) {
            v = mLayoutInflater.inflate(R.layout.item_car_card, viewGroup, false);
        } else {
            v = mLayoutInflater.inflate(R.layout.item_car, viewGroup, false);
        }

        MyViewHolder mvh = new MyViewHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        myViewHolder.tvModel.setText(mListCar.get(position).getModel());
        myViewHolder.tvBrand.setText(mListCar.get(position).getBrand());

        ControllerListener listener = new BaseControllerListener() {
            @Override
            public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
//                Log.i("LOG", "onFinalImageSet");
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
//                Log.i("LOG", "onFailure");
            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {
                super.onIntermediateImageFailed(id, throwable);
//                Log.i("LOG", "onIntermediateImageFailed");
            }

            @Override
            public void onIntermediateImageSet(String id, Object imageInfo) {
                super.onIntermediateImageSet(id, imageInfo);
//                Log.i("LOG", "onIntermediateImageSet");
            }

            @Override
            public void onRelease(String id) {
                super.onRelease(id);
//                Log.i("LOG", "onRelease");
            }

            @Override
            public void onSubmit(String id, Object callerContext) {
                super.onSubmit(id, callerContext);
//                Log.i("LOG", "onSubmit");
            }
        };

        int w = 0;
        if (myViewHolder.ivCar.getLayoutParams().width == FrameLayout.LayoutParams.MATCH_PARENT || myViewHolder.ivCar.getLayoutParams().width == FrameLayout.LayoutParams.WRAP_CONTENT) {
            Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            try {
                w = size.x;
            } catch (Exception e) {
                w = display.getWidth();
            }
        }

        Uri uri = Uri.parse(DataUrl.getUrl(mListCar.get(position).getUrlPhoto(), w));
        DraweeController dc = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setTapToRetryEnabled(true)
                .setControllerListener(listener)
                .setOldController(myViewHolder.ivCar.getController())
                .build();
        RoundingParams rp = new RoundingParams().fromCornersRadii(mRoundPixels, mRoundPixels, 0, 0);
        myViewHolder.ivCar.setController(dc);
        myViewHolder.ivCar.getHierarchy().setRoundingParams(rp);

        // borda redonda complicada
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            myViewHolder.ivCar.setImageResource(mListCar.get(position).getPhoto());
//        } else {
//            // consome demais
//            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), mListCar.get(position).getPhoto());
//            bitmap = Bitmap.createScaledBitmap(bitmap, mWidth, mHeight, false);
//
//            bitmap = ImageHelper.getRoundedCornerBitmap(mContext, bitmap, 10, mWidth, mHeight, false, false, true, true);
//            myViewHolder.ivCar.setImageBitmap(bitmap);
//        }

        if (mWithAnimation) {
            try {
                YoYo.with(Techniques.Tada).duration(700).playOn(myViewHolder.itemView).wait(500); // evitar bug com .wait(500);
            } catch (Exception e) {}
        }
    }

    @Override
    public int getItemCount() {
        return mListCar.size();
    }

    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack r) {
        mRecyclerViewOnClickListenerHack = r;
    }

    public void addListItem(Car c, int position) {
        boolean isThis = false;

        for (Car cx : mListCar) {
            if (cx.getId() == c.getId()) {
                isThis = true;
                break;
            }
        }
        if (!isThis) {
            try {
                mListCar.add(position, c);
                notifyItemInserted(position);
            } catch (Exception e) {
                Log.d("LOG", "addListItem: " + e.toString());
            }
        }
    }

    public void removeListItem(int position) {
        mListCar.remove(position);
        notifyItemRemoved(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public SimpleDraweeView ivCar;
        public TextView tvModel;
        public TextView tvBrand;
        public ImageView ivContextMenu;

        public MyViewHolder(View itemView) {
            super(itemView);

            ivCar = (SimpleDraweeView) itemView.findViewById(R.id.iv_car);
            tvModel = (TextView) itemView.findViewById(R.id.tv_model);
            tvBrand = (TextView) itemView.findViewById(R.id.tv_brand);
            ivContextMenu = (ImageView) itemView.findViewById(R.id.iv_context_menu);

            if (ivContextMenu != null) {
                ivContextMenu.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            List<ContextMenuItem> items = new ArrayList<>();
            items.add(new ContextMenuItem(R.drawable.ic_link, mContext.getString(R.string.compartilhar_link)));
            items.add(new ContextMenuItem(R.drawable.ic_email, mContext.getString(R.string.email)));
            items.add(new ContextMenuItem(R.drawable.ic_favorite, mContext.getString(R.string.favorito)));
            items.add(new ContextMenuItem(R.drawable.ic_enterprise, mContext.getString(R.string.empresa_vendas)));
            items.add(new ContextMenuItem(R.drawable.ic_discart, mContext.getString(R.string.descartar)));

            ContextMenuAdapter adapter = new ContextMenuAdapter(mContext, items);

            ListPopupWindow listPopupWindow = new ListPopupWindow(mContext);
            listPopupWindow.setAdapter(adapter);
            listPopupWindow.setAnchorView(ivContextMenu);
            listPopupWindow.setWidth((int)(240 * mScale + 0.5f));
            listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            String shareBodyText = mContext.getString(R.string.compartilhar_link) + ": http://www.google.com/search?q=" + mListCar.get(getAdapterPosition()).getModel();
                            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mContext.getString(R.string.veja_isso));
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                            mContext.startActivity(Intent.createChooser(sharingIntent, mContext.getString(R.string.compartilhar_link)));
                            break;

                        case 1:
                            Intent intent = new Intent(mContext, ContactActivity.class);
                            intent.putExtra("mCar", mListCar.get(getAdapterPosition()));
                            mContext.startActivity(intent);
                            break;

                        default:
                            Toast.makeText(mContext, getAdapterPosition() + " : " + position, Toast.LENGTH_SHORT).show();
                            break;
                    }
//                    Intent intent = new Intent(mContext, CarActivity.class);
//                    intent.putExtra("mCar", mListCar.get(getAdapterPosition()));
//                    mContext.startActivity(intent);
                }
            });
            listPopupWindow.setModal(true); // back button e fechar
            listPopupWindow.getBackground().setAlpha(0);
            listPopupWindow.show();
        }
    }

}
