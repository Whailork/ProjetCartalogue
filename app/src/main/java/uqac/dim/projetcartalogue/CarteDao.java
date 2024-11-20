package uqac.dim.projetcartalogue;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CarteDao {

    @Query("SELECT * FROM cartemodel")
    LiveData<List<CarteModel>> getAllCarte();

    @Query("SELECT * FROM cartemodel WHERE id IN (:id)")
    LiveData<List<CarteModel>> getAllIds(int[] id);

    @Query("SELECT * FROM cartemodel WHERE nom LIKE:nom")
    CarteModel findBynom(String nom);

    @Query("delete FROM cartemodel")
    void deleteCarteModel();

    @Insert
    void insertAll(CarteModel...carteModels);

    @Insert
    void addCarte(CarteModel carteModel);

    @Delete
    void delete(CarteModel carteModel);
}
