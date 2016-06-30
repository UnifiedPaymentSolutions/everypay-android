
package com.everypay.everypay.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.everypay.everypay.R;

import java.util.HashMap;
import java.util.List;



public class GenericSpinnerAdapter<T> extends ArrayAdapter {
    private static final int KEY_TEXTVIEW = 42;

    public static final int TRANSLATION_ID_NONE = 0;

    private HashMap<Integer, Integer> customTextColors;
    private int defaultColor;
    private int translationId = TRANSLATION_ID_NONE;

    public GenericSpinnerAdapter(Context context, int resource) {
        super(context, resource);
    }

    public GenericSpinnerAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public GenericSpinnerAdapter(Context context, int resource, Object[] objects) {
        super(context, resource, objects);
    }

    public GenericSpinnerAdapter(Context context, int resource, int textViewResourceId, Object[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public GenericSpinnerAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    public GenericSpinnerAdapter(Context context, int resource, int textViewResourceId, List objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public void setItems(List items) {
        clear();
        if (items != null) {
            for (Object item : items) {
                add(item);
            }
        }
    }

    public void setTranslationId(int translationId) {
        this.translationId = translationId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = super.getView(position, convertView, parent);
        // Yes, not the "best-est" idea, but spinners have only a few items


        if (customTextColors != null) {
            final int appSpecificId = R.id.start; // I just need one ID from our app
            int color = defaultColor;
            if (customTextColors.containsKey(position)) {
                // We have a custom color
                color = customTextColors.get(position);
            }

            // Try to set it
            if (view.getTag(appSpecificId) == null) {
                final TextView textTitle = (TextView) view.findViewById(android.R.id.text1);
                view.setTag(appSpecificId, textTitle);
            }
            final TextView textTitle = (TextView) view.getTag(appSpecificId);
            if (textTitle != null) {
                textTitle.setTextColor(color);
            }
        }
        return view;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // Yes, not the "best-est" idea, but spinners have only a few items
        return super.getDropDownView(position, convertView, parent);
    }

    public void setCustomTextColors(final int defaultColor, final HashMap<Integer, Integer> customTextColors) {
        this.defaultColor = defaultColor;
        this.customTextColors = customTextColors;
    }
}