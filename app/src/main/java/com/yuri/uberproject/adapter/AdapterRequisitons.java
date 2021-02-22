package com.yuri.uberproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yuri.uberproject.R;
import com.yuri.uberproject.model.Requisition;
import com.yuri.uberproject.model.User;

import org.w3c.dom.Text;

import java.util.List;

public class AdapterRequisitons extends RecyclerView.Adapter<AdapterRequisitons.MyView> {

    private List<Requisition> requisitions;
    private Context context;
    private User driver;

    public AdapterRequisitons(List<Requisition> requisitions, Context context, User driver) {
        this.requisitions = requisitions;
        this.context = context;
        this.driver = driver;
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.requisitions_layout_para_recyclerview, parent, false);
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        Requisition requisition = requisitions.get(position);
        User passageiro = requisition.getPassenger();
        holder.nome.setText(passageiro.getName());
        holder.distancia.setText(R.string.requisitions_texto_distancia);
    }

    @Override
    public int getItemCount() {
        return this.requisitions.size();
    }

    public class MyView extends RecyclerView.ViewHolder{
        TextView nome, distancia;

        public MyView(View itemView){
            super(itemView);
            nome = itemView.findViewById(R.id.textNomeRecycler);
            distancia = itemView.findViewById(R.id.textDistanciaRecycler);
        }
    }
}
