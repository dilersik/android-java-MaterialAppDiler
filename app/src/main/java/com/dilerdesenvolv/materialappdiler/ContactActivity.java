package com.dilerdesenvolv.materialappdiler;


import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.dilerdesenvolv.materialappdiler.domain.Car;
import com.dilerdesenvolv.materialappdiler.domain.Contact;
import com.dilerdesenvolv.materialappdiler.domain.WrapObjToNetwork;
import com.dilerdesenvolv.materialappdiler.extras.CarDes;
import com.dilerdesenvolv.materialappdiler.extras.Util;
import com.dilerdesenvolv.materialappdiler.network.CarAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ContactActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private View mFlProxy;
    private TextInputLayout mTilSubject, mTilMessage;
    private EditText mEtSubject, mEtMessage;
    private Contact mContact;
    private Car mCar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle(R.string.entre_em_contato);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        if (getIntent() != null && getIntent().getParcelableExtra("mCar") != null) {
            mCar = getIntent().getParcelableExtra("mCar");
        }

        mFlProxy = findViewById(R.id.fl_proxy);
        mTilSubject = (TextInputLayout) findViewById(R.id.til_subject);
        mEtSubject = (EditText) findViewById(R.id.et_subject);
        mTilMessage = (TextInputLayout) findViewById(R.id.til_message);
        mEtMessage = (EditText) findViewById(R.id.et_message);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        NetworkConnection.getInstance(this).getRequestQueue().cancelAll(ContactActivity.class.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }

        if (id == R.id.action_send) {
            if (mEtSubject.getText().toString().trim().length() == 0) {
                mEtSubject.setError(getString(R.string.campo_obrigatorio));
                mEtSubject.findFocus();
            } else if (mEtMessage.getText().toString().trim().length() == 0 ){
                mEtMessage.setError(getString(R.string.campo_obrigatorio));
                mEtMessage.findFocus();
            } else {
                mContact = new Contact();
                mContact.setEmail(Util.getEmailAccountManager(this));
                mContact.setSubject(mEtSubject.getText().toString());
                mContact.setMessage(mEtMessage.getText().toString());

                callRetrofit();

//                NetworkConnection.getInstance(this).execute( this, ContactActivity.class.getName() );
            }
        }

        return true;
    }

    private void callRetrofit() {
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
        mFlProxy.setVisibility(View.VISIBLE);

        Gson gson = new GsonBuilder().registerTypeAdapter(Car.class, new CarDes()).create();
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(Car.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        CarAPI carAPI = retrofit.create(CarAPI.class);

        WrapObjToNetwork wrapRequest = new WrapObjToNetwork(mCar, "send-contact", mContact);
        Call<Contact> call = carAPI.sendContact(wrapRequest);
        call.enqueue(new Callback<Contact>() {
            @Override
            public void onResponse(Call<Contact> call, Response<Contact> response) {
                android.support.design.widget.Snackbar.make(findViewById(R.id.cl_container),
                        response.message() + " " + getResources().getString(R.string.email_enviado_sucesso),
                        Snackbar.LENGTH_LONG)
                        .show();

                mEtSubject.setText("");
                mEtMessage.setText("");
                mFlProxy.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Contact> call, Throwable t) {
                android.support.design.widget.Snackbar.make(findViewById(R.id.cl_container),
                        getResources().getString(R.string.falhou_tente_nova),
                        Snackbar.LENGTH_LONG)
                        .show();
                mFlProxy.setVisibility(View.GONE);
            }
        });
    }

//    @Override
//    public WrapObjToNetwork doBefore() {
//        flProxy.setVisibility(View.VISIBLE);
//
//        if (UtilTCM.verifyConnection(this)) {
//            return new WrapObjToNetwork(car, "send-contact", contact));
//        }
//        return null;
//    }
//
//    @Override
//    public void doAfter(JSONArray jsonArray) {
//        flProxy.setVisibility(View.GONE);
//
//        if (jsonArray != null) {
//            try{
//                Gson gson = new Gson();
//                Response response = gson.fromJson(jsonArray.getJSONObject(0).toString(), Response.class);
//
//                android.support.design.widget.Snackbar.make(findViewById(R.id.cl_container),
//                        response.getMessage(),
//                        android.support.design.widget.Snackbar.LENGTH_LONG)
//                        .show();
//
//                if (response.getStatus()) {
//                    etSubject.setText("");
//                    etMessage.setText("");
//                }
//            } catch(JSONException e) {
//                e.printStackTrace();
//            }
//        } else {
//            android.support.design.widget.Snackbar.make(findViewById(R.id.cl_container),
//                    "Falhou, tente novamente.",
//                    android.support.design.widget.Snackbar.LENGTH_LONG)
//                    .show();
//        }
//    }

}
