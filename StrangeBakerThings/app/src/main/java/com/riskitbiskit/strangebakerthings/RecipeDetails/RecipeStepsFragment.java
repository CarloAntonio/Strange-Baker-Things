package com.riskitbiskit.strangebakerthings.RecipeDetails;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.riskitbiskit.strangebakerthings.MainActivityFiles.MainActivity;
import com.riskitbiskit.strangebakerthings.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecipeStepsFragment extends Fragment implements StepsAdapter.ListItemClickListener {

    public static final String VOLLEY_TAG = "volleyTag";

    RequestQueue mRequestQueue;
    ArrayList<Ingredient> ingredients;
    ArrayList<Instructions> instructions;
    RecyclerView mRecyclerView;
    StepsAdapter mStepsAdapter;
    int recipeNumber;
    LinearLayout mLinearLayout;
    TextView ingredientsListTV;

    OnRecipeClickListener mCallback;

    public interface  OnRecipeClickListener {
        void onRecipeClicked(int position);
    }

    public RecipeStepsFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_steps_frag, container, false);

        mLinearLayout = rootView.findViewById(R.id.view_steps_frag);

        Intent intent = getActivity().getIntent();

        //i will always be id of recipe minus 1
        recipeNumber = intent.getIntExtra(MainActivity.RECIPE_INDEX_NUMBER, 0);

        ingredients = new ArrayList<>();
        instructions = new ArrayList<>();

        mRecyclerView = rootView.findViewById(R.id.ingredient_steps_rv);
        ingredientsListTV = rootView.findViewById(R.id.ingredients_list);

        layoutCorrection();

        mRequestQueue = Volley.newRequestQueue(getContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, MainActivity.DATA_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject currentRecipe = response.getJSONObject(recipeNumber);

                    //Make Ingredients List
                    JSONArray currentIngredientsList = currentRecipe.getJSONArray("ingredients");
                    for (int i = 0; i < currentIngredientsList.length(); i++) {
                        JSONObject currentIngredient = currentIngredientsList.getJSONObject(i);
                        double quantity = currentIngredient.getDouble("quantity");
                        String measure = currentIngredient.getString("measure");
                        String ingredient = currentIngredient.getString("ingredient");

                        ingredients.add(new Ingredient(quantity, measure, ingredient));
                    }

                    String ingredientsList = "Ingredients: \n";
                    for (int i = 0; i < ingredients.size(); i++) {

                        //https://stackoverflow.com/questions/3904579/how-to-capitalize-the-first-letter-of-a-string-in-java
                        String ingredient = ingredients.get(i).getIngredient();
                        String capIngredient = ingredient.substring(0, 1).toUpperCase() + ingredient.substring(1);

                        ingredientsList = ingredientsList
                                //https://stackoverflow.com/questions/3429546/how-do-i-add-a-bullet-symbol-in-textview
                                + "\u2022 "
                                + capIngredient
                                + " ("
                                + Double.toString(ingredients.get(i).getQuantity())
                                + " "
                                + ingredients.get(i).getMeasurement()
                                + ")\n";
                    }



                    ingredientsListTV.setText(ingredientsList);

                    //Make Instructions List
                    JSONArray currentInstructionsList = currentRecipe.getJSONArray("steps");
                    for (int i = 0; i < currentInstructionsList.length(); i++) {
                        JSONObject currentStep = currentInstructionsList.getJSONObject(i);
                        int id = currentStep.getInt("id");
                        String shortDescription = currentStep.getString("shortDescription");
                        String description = currentStep.getString("description");
                        String videoUrl = currentStep.getString("videoURL");
                        String thumbnail = currentStep.getString("thumbnailURL");

                        instructions.add(new Instructions(id, shortDescription, description, videoUrl, thumbnail));
                    }

                    mStepsAdapter.notifyDataSetChanged();
                } catch (JSONException JSONE) {
                    JSONE.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error + "");
            }
        });

        jsonArrayRequest.setTag(VOLLEY_TAG);

        mRequestQueue.add(jsonArrayRequest);

        mStepsAdapter = new StepsAdapter(getContext(), instructions, this);

        mRecyclerView.setAdapter(mStepsAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnRecipeClickListener) context;
        } catch (ClassCastException CCE) {
            throw new ClassCastException(context.toString() + " must implement OnRecipeClickListener");
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        mCallback.onRecipeClicked(clickedItemIndex);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(VOLLEY_TAG);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        layoutCorrection();
    }

    //Manual Correction for orientation and screen size, code required android:configChanges="orientation|screenSize" in android manifest,
    //only solution I could think of without making a provider (wanted to see if I could do it without one).
    private void layoutCorrection() {

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        if (dpWidth < 600) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                mLinearLayout.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(16, 16, 16, 0);
                ingredientsListTV.setLayoutParams(params);

                LinearLayout.LayoutParams rvLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                rvLayoutParams.setMargins(0, 16, 0, 0);
                mRecyclerView.setLayoutParams(rvLayoutParams);

            } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
                params.setMargins(16, 16, 16, 16);
                ingredientsListTV.setLayoutParams(params);

                LinearLayout.LayoutParams rvLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
                rvLayoutParams.setMargins(0, 16, 0, 0);
                mRecyclerView.setLayoutParams(rvLayoutParams);
            }
        } else {
            mLinearLayout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(16, 16, 16, 0);
            ingredientsListTV.setLayoutParams(params);

            LinearLayout.LayoutParams rvLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            rvLayoutParams.setMargins(0, 16, 0, 0);
            mRecyclerView.setLayoutParams(rvLayoutParams);
        }
    }
}
