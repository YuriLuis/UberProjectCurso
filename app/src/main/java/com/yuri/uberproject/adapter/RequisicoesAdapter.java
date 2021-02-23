package com.yuri.uberproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yuri.uberproject.R;
import com.yuri.uberproject.model.Requisicao;
import com.yuri.uberproject.model.Usuario;

import java.util.List;

public class RequisicoesAdapter extends RecyclerView.Adapter<RequisicoesAdapter.MyView> {

    private List<Requisicao> requisicaos;
    private Context context;
    private Usuario motorista;

    public RequisicoesAdapter(List<Requisicao> requisicaos, Context context, Usuario motorista) {
        this.requisicaos = requisicaos;
        this.context = context;
        this.motorista = motorista;
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
        Requisicao requisicao = requisicaos.get(position);
        Usuario passageiro = requisicao.getPassenger();

        motorista.getLatitude();
        holder.nome.setText(passageiro.getName());
        holder.distancia.setText(R.string.requisitions_texto_distancia);
    }

    @Override
    public int getItemCount() {
        return this.requisicaos.size();
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
