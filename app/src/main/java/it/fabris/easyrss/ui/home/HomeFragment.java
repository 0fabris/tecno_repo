package it.fabris.easyrss.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.fabris.easyrss.MainActivity;
import it.fabris.easyrss.R;
import it.fabris.easyrss.rss.RSSFeed;
import it.fabris.easyrss.rss.RSSItem;
import it.fabris.easyrss.TinyDB;

/**
 * Classe Home Fragment/Schermata Homeù
 * @author Fabris
 */
public class HomeFragment extends Fragment {

    private TinyDB spref;
    private String link;
    private RSSFeed feed;
    private ArrayList<RSSItem> items_list;
    private RecyclerView recview;
    private View root;

    //Crea un AlertDialog dato un contesto, un titolo e un messaggio
    private AlertDialog showAlertDialog(Context ctx,String title, String msg){
        AlertDialog.Builder ab = new AlertDialog.Builder(ctx);
        ab.setTitle(title);
        ab.setMessage(msg);
        ab.setNeutralButton(getString(R.string.btn_cancel), (d,v)->{
            d.dismiss();
        });
        return ab.create();
    }

    //Crea un toast dato un messaggio
    private Toast createToast(String msg){
        return Toast.makeText(getContext(), msg, Toast.LENGTH_LONG);
    }

    //Metodo per aggiornare Recycler View con il Feed RSS
    private void updateList(String link){
        //Creo un Thread per non bloccare il mainThread
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try{
                            //Inizializzo il Feed
                            feed = new RSSFeed(link);

                            //Integro la lista vuota di elementi con gli Items del Feed
                            items_list.clear();
                            items_list.addAll(feed);

                            //Scateno l'evento di aggiornare la RecView
                            recview.post(new Runnable() {
                                @Override
                                public void run() {
                                    recview.getAdapter().notifyDataSetChanged();
                                    createToast(getString(R.string.home_updatedFeed)).show();
                                }
                            });
                        }
                        catch (Exception e){
                            //Errore di parsing oppure altro, es. manca connessione
                            recview.post(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            showAlertDialog(getContext(), getString(R.string.genError), getString(R.string.rss_parsingerror)).show();
                                        }
                                    }
                            );
                        }

                    }
                }
        ).start();
    }

    /**
     * Metodo OnCreateView
     * @param inflater parametro inflater, per sostituire fragment nella MainActivity
     * @param container elemento dove mettere il Fragment
     * @param savedInstanceState eventuale parametro ad es. dopo rotazione schermo
     * @return View del fragment
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //Init Root view
        root = inflater.inflate(R.layout.fragment_home, container, false);

        //Init items e Recycler view
        items_list = new ArrayList<RSSItem>();
        recview = (RecyclerView)root.findViewById(R.id.recView_cardNews);
        recview.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recview.setAdapter(new NewsItemAdapter(items_list));

        //Init TinyDB
        spref = new TinyDB(root.getContext());

        //Leggo i link salvati
        ArrayList<String> feedlinks = spref.getListString(MainActivity.rssArrayKey);

        //Prendo l'ultimo link usato
        int index = spref.getInt(MainActivity.lastFeedKey)-1;

        //Se trovo link in memoria, e l'index risulta selezionare un link correttamente
        if(feedlinks != null && index > -1 && index < feedlinks.size()) {
            //Carico il Feed
            createToast(getString(R.string.home_reloading)).show();
            link = feedlinks.get(index);
            updateList(link);
        }
        else
            //Chiedo all'utente se puo impostare un link
            showAlertDialog(getContext(),getString(R.string.welcomeTitle), getString(R.string.welcomeBody)).show();
        return root;
    }
}