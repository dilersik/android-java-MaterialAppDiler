package com.dilerdesenvolv.materialappdiler;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dilerdesenvolv.materialappdiler.domain.Car;
import com.dilerdesenvolv.materialappdiler.extras.DataUrl;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

public class CarActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, DialogInterface.OnCancelListener {

    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Car mCar;
    private Drawer mNavigationDrawerLeft;
    private MaterialDialog mMaterialDialog;
    private TextView mTvDescription, mTvTestDrive;
    private ViewGroup mRoot;
    private Button mBtTestDrive;
    private int mYear = 0, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TRANSITIONS - parece q antes do super.onCreate
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Explode trans1 = new Explode();
//            trans1.setDuration(3000);
//            Fade trans2 = new Fade();
//            trans2.setDuration(3000);
//            getWindow().setEnterTransition(trans1);
//            getWindow().setReturnTransition(trans2);
            TransitionInflater inflater = TransitionInflater.from(this);
            Transition transition = inflater.inflateTransition(R.transition.transitions);
            getWindow().setSharedElementEnterTransition(transition);

            Transition transition1 = getWindow().getSharedElementEnterTransition();
            transition1.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {}

                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onTransitionEnd(Transition transition) {
                    TransitionManager.beginDelayedTransition(mRoot, new Slide());
                    mTvDescription.setVisibility(View.VISIBLE);
                }

                @Override
                public void onTransitionCancel(Transition transition) {}

                @Override
                public void onTransitionPause(Transition transition) {}

                @Override
                public void onTransitionResume(Transition transition) {}
            });
        } else {
            mTvDescription.setVisibility(View.VISIBLE);
        }

        super.onCreate(savedInstanceState);

        Fresco.initialize(this);
        setContentView(R.layout.activity_car);

        if (savedInstanceState != null) {
            mCar = savedInstanceState.getParcelable("mCar");
        } else {
            if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getParcelable("mCar") != null) {
                mCar = getIntent().getExtras().getParcelable("mCar");
            } else {
                Toast.makeText(this, "Fail!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbarLayout.setTitle(mCar.getModel());

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle(mCar.getModel());
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        mRoot = (ViewGroup) findViewById(R.id.ll_tv_description);
        mTvDescription = (TextView) findViewById(R.id.tv_description);
//        ImageView ivCar = (ImageView) findViewById(R.id.iv_car);
        SimpleDraweeView ivCar = (SimpleDraweeView) findViewById(R.id.iv_car);
        TextView tvModel = (TextView) findViewById(R.id.tv_model);
        TextView tvBrand = (TextView) findViewById(R.id.tv_brand);
        TextView tvDescription = (TextView) findViewById(R.id.tv_description);
        Button btPhone = (Button) findViewById(R.id.bt_phone);

        btPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog = new MaterialDialog(new ContextThemeWrapper(CarActivity.this, R.style.MyAlertDialog))
                        .setTitle(R.string.telefone_empresa)
                        .setMessage(mCar.getTel())
                        .setPositiveButton(R.string.ligar, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (ActivityCompat.checkSelfPermission(CarActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(CarActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                                }

                                Intent it = new Intent(Intent.ACTION_CALL);
                                it.setData(Uri.parse("tel:" + mCar.getTel().trim()));
                                startActivity(it);
                            }
                        })
                        .setNegativeButton(R.string.voltar, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();
                            }
                        });
                mMaterialDialog.setCanceledOnTouchOutside(true);
                mMaterialDialog.show();
            }
        });

        mTvTestDrive = (TextView) findViewById(R.id.tv_test_drive);
        mBtTestDrive = (Button) findViewById(R.id.bt_test_drive);
        mBtTestDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleTestDrive(v);
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int w;
        try{
            w = size.x;
        } catch(Exception e){
            w = display.getWidth();
        }

//        Uri uri = Uri.parse("https://media.giphy.com/media/CluZlbAcA9Py0/giphy.gif");
        Uri uri = Uri.parse(DataUrl.getUrl(mCar.getUrlPhoto(), w));
        DraweeController dc = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .setOldController(ivCar.getController())
                .build();
        ivCar.setController(dc);

        tvModel.setText(mCar.getModel());
        tvBrand.setText(mCar.getBrand());
        tvDescription.setText(mCar.getDescription());

        mNavigationDrawerLeft = new DrawerBuilder().withActivity(this).build();

        // FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBodyText =  getString(R.string.compartilhar_link) + ": http://www.google.com/search?q=" + mCar.getModel();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.veja_isso));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.compartilhar_link)));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTvDescription.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_car_activity, menu);

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
        }

        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("mCar", mCar);
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionManager.beginDelayedTransition(mRoot, new Slide());
            mTvDescription.setVisibility(View.INVISIBLE);
        }

        super.onBackPressed();
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        mYear = mMonth = mDay = mHour = mMinute = 0;
        mTvTestDrive.setText("");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar tDefault = Calendar.getInstance();
        tDefault.set(mYear, mMonth, mDay, mHour, mMinute);

        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;

        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                this,
                tDefault.get(Calendar.HOUR_OF_DAY),
                tDefault.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.setOnCancelListener(this);
        timePickerDialog.show(getFragmentManager(), "TimePickerDialog");
        timePickerDialog.setTitle(getResources().getString(R.string.agendar_test_drive));
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        if (hourOfDay < 9 || hourOfDay > 18) {
            onDateSet(null, mYear, mMonth, mDay);
            Toast.makeText(this, R.string.somente_entre_9h_18h, Toast.LENGTH_LONG).show();

            return ;
        }

        mHour = hourOfDay;
        mMinute = minute;

        mTvTestDrive.setText((mDay < 10 ? "0" + mDay : mDay) + "/" +
                (mMonth + 1 < 10 ? "0" + (mMonth + 1) : mMonth) + "/" +
                mYear + " " +
                (mHour < 10 ? "0" + mHour : mHour) + ":" +
                (mMinute < 10 ? "0" + mMinute : mMinute));
    }

    private void scheduleTestDrive(View v) {
        initDateTimeData();
        Calendar cDefault = Calendar.getInstance();
        cDefault.set(mYear, mMonth, mDay);

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                this,
                cDefault.get(Calendar.YEAR),
                cDefault.get(Calendar.MONTH),
                cDefault.get(Calendar.DAY_OF_MONTH)
        );

        Calendar cMin = Calendar.getInstance();
        Calendar cMax = Calendar.getInstance();
        cMax.set(cMax.get(Calendar.YEAR), 11, 31);

        datePickerDialog.setMinDate(cMin);
        datePickerDialog.setMaxDate(cMax);

        List<Calendar> daysList = new LinkedList<>();
        Calendar[] daysArray;
        Calendar cAux = Calendar.getInstance();

        while (cAux.getTimeInMillis() <= cMax.getTimeInMillis()) {
            if (cAux.get(Calendar.DAY_OF_WEEK) != 1 && cAux.get(Calendar.DAY_OF_WEEK) != 7) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(cAux.getTimeInMillis());

                daysList.add(c);
            }
            cAux.setTimeInMillis(cAux.getTimeInMillis() + 24 * 60 * 60 * 1000);
        }

        daysArray = new Calendar[daysList.size()];
        for (int i = 0; i < daysArray.length; i ++) {
            daysArray[i] = daysList.get(i);
        }

        datePickerDialog.setSelectableDays(daysArray);
        datePickerDialog.setOnCancelListener(this);
        datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
    }

    private void initDateTimeData() {
        if (mYear == 0) {
            Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
        }
    }

}
