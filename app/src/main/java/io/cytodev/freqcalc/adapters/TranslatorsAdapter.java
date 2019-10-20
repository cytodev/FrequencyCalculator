package io.cytodev.freqcalc.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import io.cytodev.freqcalc.R;
import io.cytodev.freqcalc.objects.Translator;

/**
 * io.cytodev.freqcalc.adapters "FrequencyCalculator"
 * 2016/06/15 @ 17:19
 *
 * @author Roel Walraven <mail@cytodev.io>
 */
public class TranslatorsAdapter extends ArrayAdapter<Translator> {

    private static class ViewHolder {
        LinearLayout translatorView;
        TextView     language;
        TextView     name;
    }

    public TranslatorsAdapter(Context context, ArrayList<Translator> translators) {
        super(context, R.layout.layout_translator, translators);
    }

    @Override
    public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final Translator translator = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            viewHolder  = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_translator, parent, false);

            viewHolder.translatorView = convertView.findViewById(R.id.translator);
            viewHolder.language       = convertView.findViewById(R.id.translatorLanguage);
            viewHolder.name           = convertView.findViewById(R.id.translatorName);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.translatorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (translator != null && translator.url != null && !translator.url.equals("")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(translator.url));

                    getContext().startActivity(browserIntent);
                }
            }
        });

        if (translator != null) {
            viewHolder.language.setText(translator.language);
            viewHolder.name.setText(translator.name);
        }

        return convertView;
    }

}
