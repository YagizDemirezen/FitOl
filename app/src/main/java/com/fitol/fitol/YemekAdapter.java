package com.fitol.fitol;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class YemekAdapter extends RecyclerView.Adapter<YemekAdapter.YemekViewHolder> {

    private final List<String> yemekListesi;
    private final Context context;
    private OnItemRemovedListener onItemRemovedListener;

    public YemekAdapter(Context context, List<String> yemekListesi) {
        this.context = context;
        this.yemekListesi = yemekListesi;
    }

    @NonNull
    @Override
    public YemekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new YemekViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull YemekViewHolder holder, int position) {
        String yemek = yemekListesi.get(position);
        holder.textView.setText(yemek);
        holder.textView.setTextColor(Color.BLACK);

        holder.itemView.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Öğeyi Silmek İstiyor Musunuz?")
                    .setMessage(yemek + " öğesini silmek istiyor musunuz?")
                    .setPositiveButton("Evet", (dialog1, which) -> {
                        yemekListesi.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, yemekListesi.size());

                        if (onItemRemovedListener != null) {
                            onItemRemovedListener.onItemRemoved(yemek);
                        }
                    })
                    .setNegativeButton("Hayır", null)
                    .create();

            dialog.setOnShowListener(dialogInterface -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            });
            dialog.show();
        });

    }

    @Override
    public int getItemCount() {
        return yemekListesi.size();
    }

    static class YemekViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public YemekViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }

    public interface OnItemRemovedListener {
        void onItemRemoved(String item);
    }

    public void setOnItemRemovedListener(OnItemRemovedListener listener) {
        this.onItemRemovedListener = listener;
    }
}
