package uqac.dim.projetcartalogue;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class LoginPage extends AppCompatActivity {

    public EditText password, username;
    public int userId = -1;
    ArrayList<CarteModel> cartes;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page_layout);
        password = findViewById(R.id.passwordEntry);
        username = findViewById(R.id.usernameEntry);

        CarteBD cbd;
        CarteDao carteDao;
        cbd = CarteBD.getDataBase(getApplicationContext());
        carteDao = cbd.carteDao();

        //get les cartes dans la bd et les mettre dans la liste
        cartes = new ArrayList<>();
        carteDao.getAllCarte().observe(this, new Observer<List<CarteModel>>() {

            @Override
            public void onChanged(List<CarteModel> ca) {
                // Clear the existing list and add the new data from the database
                cartes.clear();  // Clear previous data
                if (ca != null) {
                    cartes.addAll(ca);  // Add new data
                }

            }
        });
    }

    public void loginClicked(View view){
        try{

            boolean userFound = false;
            boolean invalidEntry = false;
            String strUsername = username.getText().toString();
            String strPassword = password.getText().toString();

            if(strUsername.isEmpty() || strPassword.isEmpty()){
                invalidEntry = true;
                //display message pour dire qu'un ou plusieurs champs sont vides
            }


            //on cherche dans la bd pour voir s'il y a des cartes associées au user


            if(!invalidEntry){

                for (CarteModel model: cartes) {
                    String[] user = model.idUtilisateur.split("\\|");
                    if(user.length == 3){
                        if(user[1].equals(strUsername)){
                            //bon user
                            userFound = true;
                            if(user[2].equals(strPassword)){
                                //bon password
                                userId = Integer.parseInt(user[0]);

                                break;
                            }
                            else{
                                //mauvais  password avec un user existant
                                //on affiche un message d'erreur et on annule la connexion
                                invalidEntry = true;
                                break;
                            }
                        }

                    }
                }
                if(!userFound){
                    //on connecte et on génère un nouveau userId
                    userId = UUID.randomUUID().hashCode();
                }
            }

            if(!invalidEntry){
                Intent intent = new Intent(LoginPage.this, MainActivity.class);
                intent.putExtra("username",strUsername);
                intent.putExtra("password",strPassword);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }

        }
        catch (Exception e){
            System.out.println(e);
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Exemple de données à enregistrer
        outState.putInt("userId", userId);  // Enregistrer une chaîne
    }
}
