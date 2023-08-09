package com.example.docty.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.docty.R;

public class AppointmetListAdapter extends RecyclerView.Adapter<AppointmetListAdapter.ListAdapterVH> {

    Context context;

    public AppointmetListAdapter(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public AppointmetListAdapter.ListAdapterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.appointment_list_item,parent,false);
        return new ListAdapterVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmetListAdapter.ListAdapterVH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ListAdapterVH extends RecyclerView.ViewHolder {
        public ListAdapterVH(@NonNull View itemView) {
            super(itemView);
        }
    }
}
