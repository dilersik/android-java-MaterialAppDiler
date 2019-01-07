package com.dilerdesenvolv.materialappdiler;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.dilerdesenvolv.materialappdiler.adapters.TabsAdapter;
import com.dilerdesenvolv.materialappdiler.domain.Car;
import com.dilerdesenvolv.materialappdiler.domain.Person;
import com.dilerdesenvolv.materialappdiler.extras.SlidingTabLayout;
import com.dilerdesenvolv.materialappdiler.fragments.CarFragment;
import com.dilerdesenvolv.materialappdiler.fragments.CarLuxuryFragment;
import com.dilerdesenvolv.materialappdiler.fragments.CarOldFragment;
import com.dilerdesenvolv.materialappdiler.fragments.CarPopularFragment;
import com.dilerdesenvolv.materialappdiler.fragments.CarSportFragment;
import com.dilerdesenvolv.materialappdiler.provider.CarWidgetProvider;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.github.clans.fab.FloatingActionMenu;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.ToggleDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "LOG";
    private Toolbar mToolbar;
//    private Toolbar mToolbarBottom;
    private Drawer mNavigationDrawerLeft;
//    private Drawer mNavigationDrawerRight;
    private AccountHeader mAccHeaderNavigationLeft;
//    private int mPositionClicked = 0;
    // https://github.com/Clans/FloatingActionButton
//    private FloatingActionMenu mFab;
    private int mItemDrawerSelected = 1;
    private int mProfileDrawerSelected = 0;
    private List<PrimaryDrawerItem> mListCategories;
    private List<Person> mListProfile;
    private List<Car> mListCar;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    private OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            Toast.makeText(MainActivity.this, "onCheckedChanged: " + (isChecked ? "true" : "false"), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TRANSITIONS - parece q antes do super.onCreate
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Explode trans1 = new Explode();
//            trans1.setDuration(3000);
//            Fade trans2 = new Fade();
//            trans2.setDuration(3000);
//            getWindow().setExitTransition(trans1);
//            getWindow().setReenterTransition(trans2);
            TransitionInflater inflater = TransitionInflater.from(this);
            Transition transition = inflater.inflateTransition(R.transition.transitions);
            getWindow().setSharedElementExitTransition(transition);
        }

        super.onCreate(savedInstanceState);

        Fresco.initialize(this); // antes do contentView

        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mItemDrawerSelected = savedInstanceState.getInt("mItemDrawerSelected", 0);
            mProfileDrawerSelected = savedInstanceState.getInt("mProfileDrawerSelected", 0);
//            mListCar = savedInstanceState.getParcelableArrayList("mListCar");
        }

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle(R.string.mainActivityTitle);
        mToolbar.setSubtitle(R.string.mainActivitySubTitle);
//        mToolbar.setLogo(R.drawable.ic_launcher);
        setSupportActionBar(mToolbar);

        // SETA DE VOLTAR
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(false);

        // TOOLBAR RODAPE
//        mToolbarBottom = (Toolbar) findViewById(R.id.inc_tb_bottom);
//        mToolbarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                Intent it = null;
//
//                switch(item.getItemId()){
//                    case R.id.action_facebook:
//                        it = new Intent(Intent.ACTION_VIEW);
//                        it.setData(Uri.parse("http://www.facebook.com"));
//                        break;
//                    case R.id.action_youtube:
//                        it = new Intent(Intent.ACTION_VIEW);
//                        it.setData(Uri.parse("http://www.youtube.com"));
//                        break;
//                    case R.id.action_google_plus:
//                        it = new Intent(Intent.ACTION_VIEW);
//                        it.setData(Uri.parse("http://plus.google.com"));
//                        break;
//                    case R.id.action_linkedin:
//                        it = new Intent(Intent.ACTION_VIEW);
//                        it.setData(Uri.parse("http://www.linkedin.com"));
//                        break;
//                    case R.id.action_whatsapp:
//                        it = new Intent(Intent.ACTION_VIEW);
//                        it.setData(Uri.parse("http://www.whatsapp.com"));
//                        break;
//                }
//                startActivity(it);
//
//                return true;
//            }
//        });
//        mToolbarBottom.inflateMenu(R.menu.menu_bottom);
//        mToolbarBottom.findViewById(R.id.iv_settings).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Settings pressed", Toast.LENGTH_SHORT).show();
//            }
//        });

        // FRAGMENT
//        CarFragment frag = (CarFragment) getSupportFragmentManager().findFragmentByTag("mainFrag");
//        if (frag == null) {
//            frag = new CarFragment();
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.rl_fragment_container, frag, "mainFrag");
//            ft.commit();
//        }

        // TABS
        mViewPager = (ViewPager) findViewById(R.id.vp_tabs);
        mViewPager.setAdapter(new TabsAdapter(getSupportFragmentManager(), this));

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
//        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setCustomTabView(R.layout.tab_view, R.id.tv_tab);
        mSlidingTabLayout.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        mSlidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(getBaseContext(), R.color.colorFAB));
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                mNavigationDrawerLeft.setSelection(position); // saporra nao funciona
            }

            @Override
            public void onPageScrollStateChanged(int state) { }

        });
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setHorizontalFadingEdgeEnabled(true);
        mSlidingTabLayout.setHorizontalScrollBarEnabled(true);

        // NAVIGATION DRAWER RIGHT
//        mNavigationDrawerRight = new DrawerBuilder()
//                .withActivity(this)
////                .withToolbar(mToolbar)
//                .withDisplayBelowStatusBar(false)
//                .withActionBarDrawerToggleAnimated(true)
//                .withDrawerGravity(Gravity.END)
//                .withSavedInstance(savedInstanceState)
//                .withSelectedItem(-1)
//                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
//                    @Override
//                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
//                        Toast.makeText(MainActivity.this, "onItemClick: " + position, Toast.LENGTH_SHORT).show();
//                        return false;
//                    }
//                })
//                .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
//                    @Override
//                    public boolean onItemLongClick(View view, int position, IDrawerItem drawerItem) {
//                        Toast.makeText(MainActivity.this, "onItemLongClick: " + position, Toast.LENGTH_SHORT).show();
//                        return false;
//                    }
//                })
//                .build();
//        mNavigationDrawerRight.addItem(new SecondaryDrawerItem().withName(R.string.carros_eport).withIcon(R.drawable.car_1));
//        mNavigationDrawerRight.addItem(new SecondaryDrawerItem().withName(R.string.carros_dluxo).withIcon(R.drawable.car_2));
//        mNavigationDrawerRight.addItem(new SecondaryDrawerItem().withName(R.string.carros_pcolecion).withIcon(R.drawable.car_3));
//        mNavigationDrawerRight.addItem(new SecondaryDrawerItem().withName(R.string.carros_popular).withIcon(R.drawable.car_4));

        // HEADER NAVIGATION DRAWER LEFT
        mAccHeaderNavigationLeft = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(false)
                .withSavedInstance(savedInstanceState)
                .withThreeSmallProfileImages(false)
                .withHeaderBackground(ContextCompat.getDrawable(this, R.drawable.ct6))
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        Person aux = getPersonByEmail(mListProfile, (ProfileDrawerItem) profile);
                        mProfileDrawerSelected = getPersonPositionByEmail(mListProfile, (ProfileDrawerItem) profile);
                        mAccHeaderNavigationLeft.setBackground(ContextCompat.getDrawable(getBaseContext(), aux.getBackground()));

                        return false;
                    }
                })
//                .addProfiles(new ProfileDrawerItem().withName("Pessoa 1").withEmail("pessoa1@gmail.com").withIcon(R.drawable.person_1))
                .build();
//        mAccHeaderNavigationLeft.addProfiles(new ProfileDrawerItem().withName("Pessoa 1").withEmail("pessoa1@gmail.com").withIcon(R.drawable.person_1));
        mListProfile = getSetProfileList();
        if (mListProfile != null && mListProfile.size() > 0) {
            if(mProfileDrawerSelected != 0){
                Person aux = mListProfile.get(mProfileDrawerSelected);
                mListProfile.set(mProfileDrawerSelected, mListProfile.get(0));
                mListProfile.set(0, aux);
            }
            for (int i = 0; i < mListProfile.size(); i++) {
                mAccHeaderNavigationLeft.addProfile(mListProfile.get(i).getProfile(), i);
            }
            mAccHeaderNavigationLeft.setBackgroundRes(mListProfile.get(0).getBackground());
        }

        // NAVIGATION DRAWER LEFT
        mNavigationDrawerLeft = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withDisplayBelowStatusBar(true)
                .withActionBarDrawerToggleAnimated(true)
                .withDrawerGravity(Gravity.LEFT)
                .withSavedInstance(savedInstanceState)
//                .withActionBarDrawerToggle(false)
                .withAccountHeader(mAccHeaderNavigationLeft)
                // seta de voltar no topo
//                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
//                    @Override
//                    public boolean onNavigationClickListener(View clickedView) {
//                        return false;
//                    }
//                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        mViewPager.setCurrentItem(position - 1);
//                        Fragment frag = null;
//                        position = position - 1;
//                        mItemDrawerSelected = position;
//
//                        if (position == 0) { // ALL CARS
//                            frag = new CarFragment();
//                        } else if (position == 1){ // LUXURY CAR
//                            frag = new CarLuxuryFragment();
//                        } else if (position == 2){ // SPORT CAR
//                            frag = new CarSportFragment();
//                        } else if (position == 3){ // OLD CAR
//                            frag = new CarOldFragment();
//                        } else if (position == 4){ // POPULAR CAR
//                            frag = new CarPopularFragment();
//                        }
//
//                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                        ft.replace(R.id.rl_fragment_container, frag, "mainFrag");
//                        ft.commit();
//
//                        mToolbar.setTitle(((PrimaryDrawerItem) drawerItem).getName().toString());

                        return false;
                    }
                })
                .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(View view, int position, IDrawerItem drawerItem) {
                        Toast.makeText(MainActivity.this, "onItemLongClick: " + position, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                })
                .build();

        mListCategories = getSetCategoryList();
        if (mListCategories != null && mListCategories.size() > 0) {
            for (int i = 0; i < mListCategories.size(); i++) {
                mNavigationDrawerLeft.addItem(mListCategories.get(i));
            }
            mNavigationDrawerLeft.setSelectionAtPosition(mItemDrawerSelected);
        }
        mNavigationDrawerLeft.addItem(new SectionDrawerItem().withName(R.string.action_settings));
        mNavigationDrawerLeft.addItem(new SwitchDrawerItem().withName(R.string.notificacoes).withChecked(true).withOnCheckedChangeListener(mOnCheckedChangeListener));
        mNavigationDrawerLeft.addItem(new ToggleDrawerItem().withName(R.string.news).withChecked(true).withOnCheckedChangeListener(mOnCheckedChangeListener));
//        mNavigationDrawerLeft.addItem(new DividerDrawerItem());

        // https://github.com/Clans/FloatingActionButton
//        mFab = (FloatingActionMenu) findViewById(R.id.fab);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getIntent() != null && getIntent().getStringExtra(CarWidgetProvider.FILTER_CAR_ITEM) != null) {
            Car car = new Car();
            car.setUrlPhoto(getIntent().getStringExtra(CarWidgetProvider.FILTER_CAR_ITEM));
            setIntent(null);

            EventBus.getDefault().post(car);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

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
        int id = item.getItemId();

        if (id == R.id.action_second_activity) {
            startActivity(new Intent(this, SecondActivity.class));
        } else if (id == R.id.action_transition_activity) {
            startActivity(new Intent(this, TransitionActivity_A.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("mItemDrawerSelected", mItemDrawerSelected);
        outState.putInt("mProfileDrawerSelected", mProfileDrawerSelected);
        outState.putParcelableArrayList("mListCar", (ArrayList<Car>) mListCar);
        outState = mNavigationDrawerLeft.saveInstanceState(outState);
        outState = mAccHeaderNavigationLeft.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerLeft.isDrawerOpen()) {
            mNavigationDrawerLeft.closeDrawer();
        }
//        else if (mFab.isOpened()) { // https://github.com/Clans/FloatingActionButton
//            mFab.close(true);
//        }
        else {
            super.onBackPressed();
        }
    }

    // CATEGORIES
    private List<PrimaryDrawerItem> getSetCategoryList() {
        String[] names = new String[]{ getResources().getString(R.string.TODOS_OS_CARROS), getResources().getString(R.string.carros_dluxo), getResources().getString(R.string.carros_eport), getResources().getString(R.string.carros_pcolecion), getResources().getString(R.string.carros_popular) };
        int[] icons = new int[]{ R.drawable.car_1, R.drawable.car_1, R.drawable.car_2, R.drawable.car_3, R.drawable.car_4 };
        int[] iconsSelected = new int[]{ R.drawable.car_selected_1, R.drawable.car_selected_1, R.drawable.car_selected_2, R.drawable.car_selected_3, R.drawable.car_selected_4 };
        List<PrimaryDrawerItem> list = new ArrayList<>();

        for (int i = 0; i < names.length; i++) {
            PrimaryDrawerItem aux = new PrimaryDrawerItem();
            aux.withName(names[i]);
            aux.withIcon(icons[i]);
            aux.withTextColorRes(R.color.colorPrimarytext);
            aux.withSelectedIcon(iconsSelected[i]);
            aux.withSelectedTextColorRes(R.color.colorPrimary);

            list.add(aux);
        }
        return list;
    }

    // PERSON
    private Person getPersonByEmail(List<Person> list, ProfileDrawerItem p) {
        Person aux = null;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getProfile().getEmail().equals(p.getEmail())) {
                aux = list.get(i);
                break;
            }
        }
        return aux;
    }

    private List<Person> getSetProfileList() {
        String[] names = new String[]{ "User 1", "User 2", "User 3", "User 4" };
        String[] emails = new String[]{ "emailUser_1_@gmail.com", "emailUser_2_@gmail.com", "emailUser_3_@gmail.com", "emailUser_4_@gmail.com" };
        int[] photos = new int[]{ R.drawable.person_1, R.drawable.person_2, R.drawable.person_3, R.drawable.person_4 };
        int[] background = new int[]{ R.drawable.gallardo, R.drawable.vyron, R.drawable.corvette, R.drawable.paganni_zonda };
        List<Person> list = new ArrayList<>();

        for (int i = 0; i < names.length; i++) {
            ProfileDrawerItem aux = new ProfileDrawerItem();
            aux.withName(names[i]);
            aux.withEmail(emails[i]);
            aux.withIcon(photos[i]);

            Person p = new Person();
            p.setProfile(aux);
            p.setBackground(background[i]);

            list.add(p);
        }
        return list;
    }

    private int getPersonPositionByEmail(List<Person> list, ProfileDrawerItem p) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getProfile().getEmail().equals(p.getEmail())) {
                return i;
            }
        }
        return -1;
    }

    // CAR
//    public List<Car> getSetCarList(int qtd) {
//        return getSetCarList(qtd, 0);
//    }
////
//    public List<Car> getSetCarList(int qtd, int category) {
//        String[] models = new String[]{"Gallardo", "Vyron", "Corvette", "Pagani Zonda", "Camaro", "CT6"};
//        String[] brands = new String[]{"Lamborghini", " bugatti", "Chevrolet", "Pagani", "Chevrolet", "Cadillac"};
//        int[] categories = new int[]{2, 1, 2, 1, 1, 4, 3, 2, 4, 1};
//        int[] photos = new int[]{R.drawable.gallardo, R.drawable.vyron, R.drawable.corvette, R.drawable.paganni_zonda, R.drawable.camaro, R.drawable.ct6};
//        String[] urlPhotos = new String[]{"gallardo.jpg", "vyron.jpg", "corvette.jpg", "paganni_zonda.jpg", "camaro.jpg", "ct6.jpg"};
//        String description = "Lorem Ipsum é simplesmente uma simulação de texto da indústria tipográfica e de impressos, e vem sendo utilizado desde o século XVI, quando um impressor desconhecido pegou uma bandeja de tipos e os embaralhou para fazer um livro de modelos de tipos. Lorem Ipsum sobreviveu não só a cinco séculos, como também ao salto para a editoração eletrônica, permanecendo essencialmente inalterado. Se popularizou na década de 60, quando a Letraset lançou decalques contendo passagens de Lorem Ipsum, e mais recentemente quando passou a ser integrado a softwares de editoração eletrônica como Aldus PageMaker.";
//        List<Car> listAux = new ArrayList<>();
//
//        for (int i = 0; i < qtd; i++) {
//            Car c = new Car( models[i % models.length], brands[ i % brands.length], photos[i % models.length], Car.PATH + urlPhotos[i % models.length] );
//            c.setDescription(description);
//            c.setCategory(categories[ i % brands.length ]);
//            c.setTel("33221155");
//
//            if (category != 0 && c.getCategory() != category) {
//                continue;
//            }
//
//            listAux.add(c);
//        }
//        return listAux;
//    }
//
//    public List<Car> getCarsByCategory(int category) {
//        List<Car> listAux = new ArrayList<>();
//        for (int i = 0; i < mListCar.size() ; i++) {
//            if (category != 0 && mListCar.get(i).getCategory() != category) {
//                continue;
//            }
//
//            listAux.add(mListCar.get(i));
//        }
//        return listAux;
//    }
//
//    public List<Car> getListCars() {
//        return mListCar;
//    }

}
