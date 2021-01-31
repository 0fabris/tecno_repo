package it.fabris.easyrss.rss;

import android.content.Intent;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Classe RSSItem, singola notizia di un Feed
 * @author Fabris
 */
public class RSSItem {

    private static final String[] fieldnames = {"title","link","description","category","pubDate"};

    private String title, url, description, category, pubDate;

    /**
     * Costruttore di default, imposta a null tutte le String
     */
    public RSSItem(){
        title = url = description = category = pubDate = null;
    }

    /**
     * Costruttore che, dato un Node n, imposta automaticamente i parametri in base alle informazioni
     * @param n Node passato, dovra' contenere le informazioni della notizia
     */
    public RSSItem(Node n){
        this();
        this.parseNode(n);
    }

    /**
     * Costruttore che ottiene le informazioni passate tramite Intent
     * @param i Intent ricevuto
     */
    public RSSItem(Intent i){
        this();
        this.parseIntent(i);
    }

    /**
     * Metodo per ottenere informazioni da un Node passato
     * @param no Node passato -> corrisponde al tag <item> in XML
     */
    public void parseNode(Node no){
        Element n = (Element)no;
        try {
            this.title = n.getElementsByTagName(fieldnames[0]).item(0).getTextContent();
        } catch(NullPointerException npe){
            this.title = "";
        }
        try{
            this.url = n.getElementsByTagName(fieldnames[1]).item(0).getTextContent();
        }catch(NullPointerException npe){
            this.url = "";
        }
        try{
            this.description = n.getElementsByTagName(fieldnames[2]).item(0).getTextContent();
        }catch(NullPointerException npe){
            this.description = "";
        }
        try{
            this.category = n.getElementsByTagName(fieldnames[3]).item(0).getTextContent();
        }catch(NullPointerException npe){
            this.category = null;
        }
        try {
            this.pubDate = n.getElementsByTagName(fieldnames[4]).item(0).getTextContent();
        }catch(NullPointerException npe){
            this.pubDate = null;
        }
    }

    /**
     * Ottiene informazioni da un intent passato
     * @param i Intent ricevuto
     */
    public void parseIntent(Intent i){
        if(i != null) {
            this.title = i.hasExtra(fieldnames[0]) ? i.getStringExtra(fieldnames[0]) : null;
            this.url = i.hasExtra(fieldnames[1]) ? i.getStringExtra(fieldnames[1]) : null;
            this.description = i.hasExtra(fieldnames[2]) ? i.getStringExtra(fieldnames[2]) : null;
            this.category = i.hasExtra(fieldnames[3]) ? i.getStringExtra(fieldnames[3]) : null;
            this.pubDate = i.hasExtra(fieldnames[4]) ? i.getStringExtra(fieldnames[4]) : null;
        }
    }

    /**
     * Get Link
     * @return link della notizia
     */
    public String getLink(){
        return url;
    }

    /**
     * Get Description
     * @return descrizione notizia
     */
    public String getDescription(){
        return description;
    }

    /**
     * Get Title
     * @return Titolo della Notizia
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get Category
     * @return Categoria della notizia
     */
    public String getCategory(){
        return category;
    }

    /**
     * Get Data di pubblicazione
     * @return data di pubblicazione
     */
    public String getPubDate(){
        return pubDate;
    }

    /**
     * Aggiunge ad un intent i parametri dell'item corrente
     * @param i Intent dove aggiungere
     * @return non necessario, ritorna l'intent finale
     */
    public Intent addExtras(Intent i){
        i.putExtra(fieldnames[0],title);
        i.putExtra(fieldnames[1],url);
        i.putExtra(fieldnames[2],description);
        i.putExtra(fieldnames[3],category);
        i.putExtra(fieldnames[4],pubDate);
        return i;
    }

    /**
     * Metodo toString
     * @return Titolo
     */
    public String toString(){
        return getTitle();
    }
}
