package com.example.apptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    List<String> listRecycler = new ArrayList<>();
    List<String> listSpinner = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        for (int i = 0; i < 5; i++) {
            listSpinner.add("item " + i);
        }

        final MyRecyclerViewAdapter recyclerViewAdapter = new MyRecyclerViewAdapter(listRecycler);

        RecyclerView recyclerView = findViewById(R.id.recycler1);
        recyclerView.setAdapter(recyclerViewAdapter);

        final ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listSpinner);

        final Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(spinnerAdapter);


        recyclerViewAdapter.listener = new MyRecyclerViewAdapter.RecyclerLister() {
            @Override
            public void OnClickListener(int position) {
                listSpinner.add(listRecycler.get(position));
                spinnerAdapter.notifyDataSetChanged();

                listRecycler.remove(position);
                recyclerViewAdapter.notifyItemRemoved(position);
            }
        };


        spinner.postDelayed(new Runnable() {
            @Override
            public void run() {
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        listRecycler.add(listSpinner.get(position));
                        recyclerViewAdapter.notifyItemInserted(listRecycler.size()-1);

                        listSpinner.remove(position);
                        spinnerAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        int a = 0;
                        String b = String.valueOf(a);
                    }
                });
            }
        }, 500);


    }
}


class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {


    List<String> data;
    RecyclerLister listener;

    MyRecyclerViewAdapter(List<String> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.myTextView.setText(data.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnClickListener(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public interface RecyclerLister {
        void OnClickListener(int position);
    }


    // stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(android.R.id.text1);
        }

    }
}

