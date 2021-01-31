package it.fabris.easyrss.rss;

import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Classe RSSFeed
 * Deriva da ArrayList e contiene RSSItems
 * @author Fabris
 */
public class RSSFeed extends ArrayList<RSSItem>{
    /**
     * Url passato nel costruttore
     */
    protected String url;

    private final static String label_log_parse = "XMLParsing", label_log_url="LinkParsingResult";
    private boolean parsingResult;

    /**
     * Costruttore di default, istanzia l'oggetto
     */
    public RSSFeed() {
    }

    /**
     * Costruttore in base ad un url dato
     * @param url url di un file xml in formato rss
     * @throws Exception Nel caso che il parsing non vada a buon fine
     */
    public RSSFeed(String url) throws Exception{
        this.url = url;
        if(!this.parseURL()) {
            this.clear();
            throw new Exception();
        }
    }

    /**
     * Ottiene l'url del feed
     * @return url del feed
     */
    protected String getURL(){
        return this.url;
    }

    //Metodi Privati per fare il parsing di un URL
    private boolean parseURL() {
        //Semaforo per la sincronizzazione di un evento asincrono
        Semaphore s = new Semaphore(0);

        //Pulisco elementi presenti
        parsingResult=true;
        this.clear();

        //Richiesta HTTP GET
        OkHttpClient ok = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        //Gestore risposta
        ok.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                s.release();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String rss_page = response.body().string();
                    if(!parseXML(rss_page)) parsingResult = false;
                }
                s.release();
            }
        });

        //Quando riceve lo sblocco del semaforo -> risposta ricevuta
        try {
            s.acquire();
            parsingResult = parsingResult && (this.size() > 0);
            Log.d(label_log_url,"OK");
        } catch (Exception e) {
            //Lista vuota -> rss vuoto o errori in fase di parse
            Log.d(label_log_url,"NOK - "+e.toString());
            parsingResult = false;
        }
        return parsingResult; //true se correttamente interpretato, false altrimenti
    }

    //Metodo per interpretare un file XML
    private boolean parseXML(String rss_page){
        try {
            //Istanzio oggetti per parsing XML
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            StringBuilder sb = new StringBuilder(rss_page);
            ByteArrayInputStream input = new ByteArrayInputStream(
                    sb.toString().getBytes("UTF-8"));
            Document doc = builder.parse(input);

            //Prendo una lista di tutti i nodi "item"
            NodeList nList = doc.getElementsByTagName("item");

            //Per ogni nodo si suppone che sia un RSSItem, e quindi aggiungo alla lista corrente il nuovo item
            for(int i = 0; i < nList.getLength(); i++){
                this.add(new RSSItem(nList.item(i)));
            }

            //Tutto correttamente riuscito
            return true;
        } catch (SAXParseException spe) {
            //Formato file non corretto
            Log.d(label_log_parse, spe.toString());
        } catch (Exception e){
            Log.d(label_log_parse,e.toString());
        }
        return false;
    }

}
