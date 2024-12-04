package uqac.dim.projetcartalogue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

public class CardViewHolder extends RecyclerView.ViewHolder {
    ImageView imageCarte;
    TextView numero;
    TextView nomCarte;
    TextView typePokemon;
    View view;
    ConstraintLayout backgroundLayout;

    CardViewHolder(View itemView)
    {
        super(itemView);
        imageCarte = (ImageView) itemView.findViewById(R.id.imageCarte);
        numero = (TextView)itemView.findViewById(R.id.numero);
        nomCarte = (TextView)itemView.findViewById(R.id.nomCarte);
        typePokemon = (TextView)itemView.findViewById(R.id.typePokemon);
        backgroundLayout = (ConstraintLayout)itemView.findViewById(R.id.backgroundLayout);
        view  = itemView;

    }


}
