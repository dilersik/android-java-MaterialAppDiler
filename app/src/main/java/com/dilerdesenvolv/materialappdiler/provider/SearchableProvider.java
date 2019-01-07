package com.dilerdesenvolv.materialappdiler.provider;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by T-Gamer on 01/08/2016.
 */
public class SearchableProvider extends SearchRecentSuggestionsProvider {

    public static final String AUTHORITY = "com.dilerdesenvolv.materialappdiler.provider.SearchableProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public SearchableProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }

}
