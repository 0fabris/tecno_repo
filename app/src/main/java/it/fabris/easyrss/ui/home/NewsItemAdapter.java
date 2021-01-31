package it.fabris.easyrss.ui.home;

import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.fabris.easyrss.R;
import it.fabris.easyrss.rss.RSSItem;

/**
 * Adapter RSSItem a CardView nella RecyclerView
 * @author Fabris
 */
public class NewsItemAdapter extends RecyclerView.Adapter<NewsItemAdapter.CardHolder> {

    /**
     * Classe interna
     * @author Fabris
     */
    public static class CardHolder extends RecyclerView.ViewHolder{

        /**
         * Titolo Visualizzato
         */
        protected TextView mTitle;

        /**
         * Sottotitolo
         */
        protected TextView mSub;

        /**
         * Elemento RSSItem a cui la card fa riferimento
         */
        protected RSSItem item;

        /**
         * Costruttore Card
         * @param view View di Creazione
         */
        public CardHolder(View view){
            super(view);
            mTitle = view.findViewById(R.id.txtCardTitle);
            mSub = view.findViewById(R.id.txtCardSub);
            view.setOnClickListener((v)->{
                //Apre altra activity per vedere info
                Intent i = new Intent(v.getContext(), NewsActivity.class);
                i = item.addExtras(i);
                v.getContext().startActivity(i);
            });
        }

        /**
         * Trasforma un RSSItem in una Card
         * @param itm RSSItem di partenza
         */
        protected void parseRSSItem(RSSItem itm){
            item = itm;
            String title = item.getTitle();

            mTitle.setText(title);
            String sub = item.getDescription();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                mSub.setText(Html.fromHtml(sub,Html.FROM_HTML_MODE_LEGACY));
            } else {
                mSub.setText(Html.fromHtml(sub));
            }
        }
    }

    private final ArrayList<RSSItem> mValues;

    /**
     * Costruttore
     * @param items ArrayList di RSSItem da visualizzare
     */
    public NewsItemAdapter(ArrayList<RSSItem> items){
        mValues = items;
    }

    /**
     * Metodo creazione di ViewHolder (Card)
     * @param parent ViewGroup di partenza
     * @param viewType tipo di View
     * @return Card creata
     */
    @NonNull
    @Override
    public NewsItemAdapter.CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        return new CardHolder(view);
    }

    /**
     * metodo BindViewHolder
     * @param holder Card
     * @param position Posizione nell'array iniziale
     */
    @Override
    public void onBindViewHolder(@NonNull NewsItemAdapter.CardHolder holder, int position) {
        holder.parseRSSItem(mValues.get(position));
    }

    /**
     * Metodo per ottenere il numero di Items
     * @return numero di Items
     */
    @Override
    public int getItemCount() {
        return mValues == null ? 0 : mValues.size();
    }
}
