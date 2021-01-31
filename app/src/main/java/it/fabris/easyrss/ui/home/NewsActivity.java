package it.fabris.easyrss.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import it.fabris.easyrss.R;
import it.fabris.easyrss.rss.RSSItem;

/**
 * Classe News Activity
 * @author Fabris
 */
public class NewsActivity extends AppCompatActivity {
    private RSSItem rss;

    /**
     * Metodo per gestire click sulla freccetta per tornare indietro nella NavBar
     * @param item Elemento cliccato
     * @return operazione completata
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); //Chiudo activity
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Metodo lanciato alla creazione dell'Activity
     * @param savedInstanceState eventuale parametro per creazione dopo una distruzione di activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Imposto layout in base orientamento schermo
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            setContentView(R.layout.activity_news_vertical);
        else
            setContentView(R.layout.activity_news_horizontal);

        //Attivo pulsante Back NavBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Ricevo Intent e creo RSSItem dato un Intent
        Intent rec = getIntent();
        rss = new RSSItem(rec);

        //Istanzio elementi grafici
        TextView txtTitle = (TextView)findViewById(R.id.txtNewsTitle);
        TextView txtBody = (TextView)findViewById(R.id.txtTestoArticolo);
        TextView txtCateg = (TextView)findViewById(R.id.txtCategoryArticle);
        TextView txtDate = (TextView)findViewById(R.id.txtDateArticle);
        LinearLayout btnLink = (LinearLayout) findViewById(R.id.llopLink);
        LinearLayout btnShare = (LinearLayout) findViewById(R.id.llshare);

        //Visualizzo informazioni RSSItem
        txtTitle.setText(rss.getTitle());
        //Visualizzo descrizione in formato HTML
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            txtBody.setText(Html.fromHtml(rss.getDescription(),Html.FROM_HTML_MODE_LEGACY));
        } else {
            txtBody.setText(Html.fromHtml(rss.getDescription()));
        }
        txtBody.setEnabled(false); //Disabilito scrittura

        //Check categoria e data pubblicazione
        if(rss.getCategory() != null){
            txtCateg.setText(getString(R.string.news_category) + " " + rss.getCategory());
        }

        if(rss.getPubDate() != null){
            txtDate.setText(getString(R.string.news_pubDate) + " " + rss.getPubDate());
        }

        //Evento click per aprire il link dell'item
        btnLink.setOnClickListener((v)->{
            Uri uri = Uri.parse(rss.getLink()); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            v.getContext().startActivity(intent);
        });


        //Evento condivisione della notizia
        btnShare.setOnClickListener((v)->{
            Uri uri = Uri.parse(rss.getLink()); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.news_sharedstring)+ " " + getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            v.getContext().startActivity(Intent.createChooser(intent,getString(R.string.news_shareLink)));
        });

    }
}