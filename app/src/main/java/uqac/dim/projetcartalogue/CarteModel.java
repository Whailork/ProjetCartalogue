package uqac.dim.projetcartalogue;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Objects;
import java.util.UUID;

@Entity
public class CarteModel implements Parcelable {
    @PrimaryKey
    int id;

    @ColumnInfo(name = "idUtilisateur")
    String idUtilisateur;


    @ColumnInfo (name = "numero")
    String numero;

    @ColumnInfo (name = "nom")
    String nom;

    @ColumnInfo (name = "type")
    String type;

    @ColumnInfo (name = "imageId")
    int imageId ;

    @ColumnInfo (name = "stage")
    String stage;

    @ColumnInfo (name = "alolan")
    boolean alolan;

    @ColumnInfo (name = "deck")
    boolean deck;

    @ColumnInfo (name = "evolvesFrom")
    String evolvesFrom;

    @ColumnInfo (name = "pv")
    int pv;

    @ColumnInfo (name = "pokemonType")
    String pokemonType;

    @ColumnInfo (name = "height")
    String height;

    @ColumnInfo (name = "weight")
    String weight;

    @ColumnInfo (name = "imgBitMap")
    Bitmap imgBitmap;

    @ColumnInfo (name = "attack1")
    public String attack1;

    @ColumnInfo (name = "attack2")
    public String attack2;

    @ColumnInfo (name = "attack3")
    public String attack3;

    @ColumnInfo (name = "attack4")
    public String attack4;

    @ColumnInfo (name = "description")
    String description;

    public CarteModel(int id,int imageId, String numero, String nom, String type) {
        this.imageId = imageId;
        this.type = type;
        this.nom = nom;
        this.numero = numero;

    }
    public CarteModel(){
        id = UUID.randomUUID().hashCode();
        idUtilisateur = "";
        imageId = -1;
        type = "";
        nom = "";
        numero = "";
        attack1 = "";
        attack2 = "";
        attack3 = "";
        attack4 = "";
        deck = false;
    }

    protected CarteModel(Parcel in) {
        id = in.readInt();
        numero = in.readString();
        nom = in.readString();
        type = in.readString();
        imageId = in.readInt();
        stage = in.readString();
        alolan = in.readByte() != 0;
        deck = in.readByte() != 0;
        evolvesFrom = in.readString();
        pv = in.readInt();
        pokemonType = in.readString();
        height = in.readString();
        weight = in.readString();
        //imgBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        description = in.readString();
    }

    public static final Creator<CarteModel> CREATOR = new Creator<CarteModel>() {
        @Override
        public CarteModel createFromParcel(Parcel in) {
            return new CarteModel(in);
        }

        @Override
        public CarteModel[] newArray(int size) {
            return new CarteModel[size];
        }
    };

    public String getNumero() {
        return numero;
    }

    public String getNom() {
        return nom;
    }

    public String getType() {
        return type;
    }

    public int getImage() {
        return imageId;
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

    public void setImage(int image) {
        this.imageId = image;
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

    public boolean isDeck() {
        return deck;
    }

    public void setDeck(boolean deck) {
        this.deck = deck;
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

    public Bitmap getImgBitmap() {
        return imgBitmap;
    }

    public void setImgBitmap(Bitmap imgBitmap) {
        this.imgBitmap = imgBitmap;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(numero);
        parcel.writeString(nom);
        parcel.writeString(type);
        parcel.writeInt(imageId);
        parcel.writeString(stage);
        parcel.writeByte((byte) (alolan ? 1 : 0));
        parcel.writeString(evolvesFrom);
        parcel.writeInt(pv);
        parcel.writeString(pokemonType);
        parcel.writeString(height);
        parcel.writeString(weight);
        //parcel.writeValue(imgBitmap);
        parcel.writeString(description);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarteModel that = (CarteModel) o;
        return id == that.id && imageId == that.imageId && alolan == that.alolan && pv == that.pv && Objects.equals(numero, that.numero) && Objects.equals(nom, that.nom) && Objects.equals(type, that.type) && Objects.equals(stage, that.stage) && Objects.equals(evolvesFrom, that.evolvesFrom) && Objects.equals(pokemonType, that.pokemonType) && Objects.equals(height, that.height) && Objects.equals(weight, that.weight) && Objects.equals(imgBitmap, that.imgBitmap) && Objects.equals(description, that.description);
    }

}
