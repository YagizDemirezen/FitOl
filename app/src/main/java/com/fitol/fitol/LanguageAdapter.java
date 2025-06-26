package com.fitol.fitol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.fitol.FitOl.R;
import java.util.List;

public class LanguageAdapter extends ArrayAdapter<LanguageItem> {

    public LanguageAdapter(Context context, List<LanguageItem> languageList) {
        super(context, 0, languageList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.language_spinner_item, parent, false);
        }

        LanguageItem languageItem = getItem(position);
        ImageView imageView = convertView.findViewById(R.id.languageIcon);
        TextView textView = convertView.findViewById(R.id.languageName);

        if (languageItem != null) {
            imageView.setImageResource(languageItem.getFlagImage());
            textView.setText(languageItem.getLanguageName());
        }

        return convertView;
    }
}