package uqac.dim.projetcartalogue;

import android.media.Image;

public class carteModel {
    String numero;
    String nom;
    String type;
    Image image ;

    public carteModel(Image image, String numero, String nom, String type) {
        this.image = image;
        this.type = type;
        this.nom = nom;
        this.numero = numero;
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
}
