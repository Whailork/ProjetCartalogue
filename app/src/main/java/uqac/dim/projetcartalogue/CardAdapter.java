package uqac.dim.projetcartalogue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {
    List<CarteModel> listCarte = Collections.emptyList();
    Context context;

    public CardAdapter(List<CarteModel> list, Context context){
        this.listCarte = list;
        this.context = context;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View inflatedView = layoutInflater.inflate(R.layout.recycler_view_row,parent,false);


        return new CardViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(final CardViewHolder viewHolder, final int position) {
        viewHolder.imageCarte.setImageResource(listCarte.get(position).imageId);
        viewHolder.numero.setText(listCarte.get(position).numero);
        viewHolder.nomCarte.setText(listCarte.get(position).nom);
        viewHolder.typePokemon.setText(listCarte.get(position).type);
        viewHolder.backgroundLayout.setBackgroundColor(ContextCompat.getColor(context,context.getResources().getIdentifier(viewHolder.typePokemon.getText().toString(), "color", context.getPackageName())));

    }

    @Override
    public int getItemCount() {
        return listCarte.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
