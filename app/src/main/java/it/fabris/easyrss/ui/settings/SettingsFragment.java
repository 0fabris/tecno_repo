package it.fabris.easyrss.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import it.fabris.easyrss.MainActivity;
import it.fabris.easyrss.R;
import it.fabris.easyrss.TinyDB;

/**
 * Classe Settings Fragment
 * @author Fabris
 */
public class SettingsFragment extends Fragment {

    /**
     * Elemento grafico Spinner
     */
    protected Spinner spItems;

    /**
     * Elemento Grafico Bottone Aggiungi Link
     */
    protected Button btnaddLink;

    /**
     * Elemento Grafico Rimuovi Link
     */
    protected Button removeLink;

    /**
     * Elemento grafico Textview dove inserire il link
     */
    protected TextView txtLink;

    private static TinyDB spref;

    //Metodo per caricare gli elementi dello spinner
    private void loadItems(View v){
        ArrayList<String> items = new ArrayList<String>();
        items.add(getString(R.string.settings_addLink));
        ArrayList<String> links = spref.getListString(MainActivity.rssArrayKey);
        if(links != null && links.size()!=0)
            for(String l : links)
                items.add(l);

        spItems.setAdapter(new ArrayAdapter<String>(v.getContext(),android.R.layout.simple_spinner_item, items));
    }

    //Metodo per vedere se attivare btn e txtView
    private void checkInsertItem(){
        btnaddLink.setEnabled(spItems.getSelectedItemPosition() == 0);
        txtLink.setEnabled(spItems.getSelectedItemPosition() == 0);
        removeLink.setEnabled(spItems.getSelectedItemPosition() != 0);
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
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        //TinyDB
        spref = new TinyDB(root.getContext());

        //Init Elementi Grafici
        btnaddLink = (Button)root.findViewById(R.id.btn_addLink);
        spItems = (Spinner)root.findViewById(R.id.spin_links);
        txtLink = (TextView)root.findViewById(R.id.editTxt_addLink);
        removeLink = (Button) root.findViewById(R.id.btn_removeLink);

        //Carico elementi spinner
        loadItems(root);
        //Se ho gia' scelto almeno una volta un link, preseleziono quello
        spItems.setSelection(spref.getInt(MainActivity.lastFeedKey),true);
        checkInsertItem();

        //Gestore aggiunta link
        btnaddLink.setOnClickListener((v)->{
            String lnk = (txtLink.getText().toString().trim()); //se link valido;
            //Cerco Keyword nel link per escludere molti errori di parsing successivamente
            if(!lnk.contains("xml") && !lnk.contains("rss") && !lnk.contains("feed") && !lnk.contains("http")) {
                Toast.makeText(v.getContext(), getString(R.string.settings_errorLink),Toast.LENGTH_LONG).show();
            }
            else
            {
                //Se passa il controllo
                //Prendo la lista di elementi e aggiungo il nuovo link in coda
                ArrayList<String> items = spref.getListString(MainActivity.rssArrayKey);
                items.add(lnk);
                spref.putListString(MainActivity.rssArrayKey,items);

                //Scelgo automaticamente il nuovo link
                spref.putInt(MainActivity.lastFeedKey,items.size()-1);
                Toast.makeText(v.getContext(),getString(R.string.settings_addedLink),Toast.LENGTH_LONG).show();
                txtLink.setText("");
            }

            //Rivisualizzo elementi nello spinner
            loadItems(v);
            spItems.setSelection(spref.getInt(MainActivity.lastFeedKey),true);
        });


        //Gestore selezione link
        spItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position != 0) {
                    spref.putInt(MainActivity.lastFeedKey,position);
                    Toast.makeText(getContext(), getString(R.string.settings_okFeedChoose), Toast.LENGTH_LONG).show();
                }
                checkInsertItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                checkInsertItem();
                return;
            }
        });

        //Gestione rimozione link
        removeLink.setOnClickListener((v)->{
            int pos = spItems.getSelectedItemPosition()-1;
            if(pos != -1){
                ArrayList<String> items = spref.getListString(MainActivity.rssArrayKey);
                items.remove(pos);
                spref.putListString(MainActivity.rssArrayKey,items);
                if(spref.getInt(MainActivity.lastFeedKey) >= pos)
                    spref.putInt(MainActivity.lastFeedKey,items.size()-1);
                Toast.makeText(getContext(), getString(R.string.settings_okFeedRemoved), Toast.LENGTH_LONG).show();
            }
            loadItems(v);
            spItems.setSelection(spref.getInt(MainActivity.lastFeedKey)+1,true);
        });
        return root;
    }
}