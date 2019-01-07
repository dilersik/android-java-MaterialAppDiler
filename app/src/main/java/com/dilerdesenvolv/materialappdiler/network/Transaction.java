package com.dilerdesenvolv.materialappdiler.network;

import com.dilerdesenvolv.materialappdiler.domain.WrapObjToNetwork;

import org.json.JSONArray;

/**
 * Created by T-Gamer on 13/08/2016.
 */
public interface Transaction {

    public WrapObjToNetwork doBefore();

    public void doAfter(JSONArray jsonArray);

}
