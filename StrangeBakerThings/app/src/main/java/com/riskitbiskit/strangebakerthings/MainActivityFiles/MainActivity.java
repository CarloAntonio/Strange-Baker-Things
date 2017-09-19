package com.riskitbiskit.strangebakerthings.MainActivityFiles;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.riskitbiskit.strangebakerthings.R;
import com.riskitbiskit.strangebakerthings.RecipeDetails.RecipeDetails;
import com.riskitbiskit.strangebakerthings.TestingFiles.SimpleIdlingResource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static final String RECIPE_INDEX_NUMBER = "recipeNumber";
    public static final String RECIPE_LIST = "recipeList";
    public static final String DATA_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    RequestQueue mRequestQueue;
    RecipeArrayAdapter mRecipeArrayAdapter;
    List<Recipe> recipes;
    SimpleIdlingResource mIdlingResource;

    //Menu Variables
    private List<String> recipeNames;
    private ArrayAdapter<String> arrayAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    //Butterknife Variables
    @BindView(R.id.recipe_list_gv)
    GridView recipeListGV;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.left_drawer)
    ListView mDrawerList;

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        recipes = new ArrayList<>();
        recipeNames = new ArrayList<>();
        mActivityTitle = getTitle().toString();


        //Used developer.android.com/training/volley to learn and implement this code
        mRequestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, DATA_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject currentRecipe = response.getJSONObject(i);
                        int recipeId = currentRecipe.getInt("id");
                        String recipeName = currentRecipe.getString("name");
                        recipes.add(new Recipe(recipeId, recipeName));
                        recipeNames.add(recipeName);
                    }

                    mRecipeArrayAdapter.notifyDataSetChanged();

                    //Sets up hamburger menu
                    //Refactored code from: https://developer.android.com/training/implementing-navigation/nav-drawer.html
                    //and http://blog.teamtreehouse.com/add-navigation-drawer-android
                    setupMenuItems();
                    setupMenu();

                    mDrawerToggle.setDrawerIndicatorEnabled(true);

                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);

                    mDrawerToggle.syncState();

                } catch (JSONException JSONE) {
                    JSONE.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, error + "");
            }
        }
        );

        mRequestQueue.add(jsonArrayRequest);

        mRecipeArrayAdapter = new RecipeArrayAdapter(this, R.layout.recipe_item, recipes);

        recipeListGV.setAdapter(mRecipeArrayAdapter);

        recipeListGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), RecipeDetails.class);
                intent.putExtra(RECIPE_INDEX_NUMBER, position);
                intent.putStringArrayListExtra(RECIPE_LIST, (ArrayList<String>) recipeNames);
                startActivity(intent);
            }
        });
    }

    private void setupMenuItems() {
        //Setup Hamburger Menu
        arrayAdapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, recipeNames);
        mDrawerList.setAdapter(arrayAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getBaseContext(), RecipeDetails.class);
                intent.putExtra(RECIPE_INDEX_NUMBER, position);
                intent.putStringArrayListExtra(RECIPE_LIST, (ArrayList<String>) recipeNames);
                startActivity(intent);
            }
        });
    }

    private void setupMenu() {
        //Setup Hamburger Image
        mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation");
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(mActivityTitle);
            }
        };
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
