package uqac.dim.projetcartalogue;

import android.media.Image;

import java.util.Dictionary;
import java.util.Hashtable;

public class carteModel {
    String numero;
    String nom;
    String type;
    Image image ;
    String stage;
    boolean alolan;
    String evolvesFrom;
    int pv;
    String pokemonType;
    String height;
    String weight;
    public Hashtable<String,String> attacks;

    public carteModel(Image image, String numero, String nom, String type) {
        this.image = image;
        this.type = type;
        this.nom = nom;
        this.numero = numero;
        attacks = new Hashtable<>();
    }
    public carteModel(){
        image = null;
        type = "";
        nom = "";
        numero = "";
        attacks = new Hashtable<>();
    }

    public String getNumero() {
        return numero;
    }

    public String getNom() {
        return nom;
    }

    public String getType() {
        return type;
    }

    public Image getImage() {
        return image;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public boolean isAlolan() {
        return alolan;
    }

    public void setAlolan(boolean alolan) {
        this.alolan = alolan;
    }

    public String getEvolvesFrom() {
        return evolvesFrom;
    }

    public void setEvolvesFrom(String evolvesFrom) {
        this.evolvesFrom = evolvesFrom;
    }

    public int getPv() {
        return pv;
    }

    public void setPv(int pv) {
        this.pv = pv;
    }

    public String getPokemonType() {
        return pokemonType;
    }

    public void setPokemonType(String pokemonType) {
        this.pokemonType = pokemonType;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

}
