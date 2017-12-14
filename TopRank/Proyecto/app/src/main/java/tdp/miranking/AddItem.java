package tdp.miranking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

/**
 * This activity is used to add a new item into the database
 *
 * @author Matias Massetti
 * @version Jan-2017
 */

public class AddItem extends Activity {

    /**
     * Called when the activity is starting
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
    }

    /**
     * Saves the new item into the database
     */
    public void addEntity(View v) {

        EditText name = (EditText) findViewById(R.id.et_add_name);
        String nameStr= name.getText().toString();
        if (nameStr.matches("")) {
            Toast.makeText(this, "Debe ingresar un nombre!", Toast.LENGTH_SHORT).show();
        }
        else {
            RatingBar valuation = (RatingBar) findViewById(R.id.rb_add_valuation);
            DBAdapter db = new DBAdapter(this);
            db.open();
            db.insertRecord(nameStr, Float.toString(valuation.getRating()));
            db.close();
            name.setText("");
            valuation.setRating(0);

            Toast.makeText(AddItem.this, "Elemento guardado", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Returns to the main activity
     */
    public void goBack(View v) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

}

