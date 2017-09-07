package com.riskitbiskit.strangebakerthings.MainActivityFiles;

import android.app.DownloadManager;
import android.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.riskitbiskit.strangebakerthings.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    RequestQueue mRequestQueue;
    RecipeArrayAdapter mRecipeArrayAdapter;
    List<Recipe> recipes;
    GridView recipeListGV;

    //Butterknife vars
//    @BindView(R.id.recipe_list_gv)
//    GridView recipeListGV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ButterKnife.bind(this);
        recipeListGV = (GridView) findViewById(R.id.recipe_list_gv);

        //Used developer.android.com/training/volley to learn and implement this code
        mRequestQueue = Volley.newRequestQueue(this);
        String url = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

        recipes = new ArrayList<>();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject currentRecipe = response.getJSONObject(i);
                        int recipeId = currentRecipe.getInt("id");
                        String recipeName = currentRecipe.getString("name");
                        recipes.add(new Recipe(recipeId, recipeName));
                    }
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
    }
}
