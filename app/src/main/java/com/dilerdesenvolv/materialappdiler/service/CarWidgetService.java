package com.dilerdesenvolv.materialappdiler.service;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.dilerdesenvolv.materialappdiler.adapters.CarWidgetFactoryAdapter;

/**
 * Created by T-Gamer on 05/08/2016.
 */
public class CarWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new CarWidgetFactoryAdapter(this, intent);
    }

}
