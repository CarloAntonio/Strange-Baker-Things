package com.riskitbiskit.strangebakerthings.MainActivityFiles;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.riskitbiskit.strangebakerthings.R;

import java.util.List;

import butterknife.BindView;


public class RecipeArrayAdapter extends ArrayAdapter<Recipe> {

    public RecipeArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Recipe> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.recipe_item, parent, false);
        }

        Recipe currentRecipe = getItem(position);

        TextView recipeNameTV = listItemView.findViewById(R.id.recipe_name);
        recipeNameTV.setText(currentRecipe.getName());

        return listItemView;
    }
}
