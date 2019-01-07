package com.dilerdesenvolv.materialappdiler;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class SecondActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Toolbar mToolbarBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("Second activity");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbarBottom = (Toolbar) findViewById(R.id.inc_tb_bottom);
        mToolbarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent it = null;

                switch(item.getItemId()){
                    case R.id.action_facebook:
                        it = new Intent(Intent.ACTION_VIEW);
                        it.setData(Uri.parse("https://www.facebook.com/dilersik/"));
                        break;
                    case R.id.action_youtube:
                        it = new Intent(Intent.ACTION_VIEW);
                        it.setData(Uri.parse("https://www.youtube.com/channel/UCzKrzN1naIn9UtFCbjQzJMg/"));
                        break;
                    case R.id.action_google_plus:
                        Toast.makeText(SecondActivity.this, R.string.nao_tenho_gplus, Toast.LENGTH_LONG).show();
                        break;
                    case R.id.action_linkedin:
                        it = new Intent(Intent.ACTION_VIEW);
                        it.setData(Uri.parse("https://www.linkedin.com/in/dilermando-sikora/"));
                        break;
                    case R.id.action_whatsapp:
                        Toast.makeText(SecondActivity.this, R.string.telefone_9, Toast.LENGTH_LONG).show();
                        break;
                }
                startActivity(it);

                return true;
            }
        });
        mToolbarBottom.inflateMenu(R.menu.menu_bottom);
        mToolbarBottom.findViewById(R.id.iv_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SecondActivity.this, "Settings pressed", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setBackgroundResource(R.drawable.toolbar_rounded_corners);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_second, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return true;
    }

}
