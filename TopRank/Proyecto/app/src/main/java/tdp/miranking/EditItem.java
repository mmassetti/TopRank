package tdp.miranking;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

/**
 * This activity is used to edit an existing item
 *
 * @author Matias Massetti
 * @version Jan-2017
 */

public class EditItem extends Activity {

    /**
     * Local variable
     */
    int id_edit = 0;


    /**
     * Called when the activity is starting
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            id_edit = b.getInt("idToEdit");
            getData(id_edit);
        }
    }

    /**
     * Gets the specific item that will be edited
     */
    public void getData(int id) {
        DBAdapter db = new DBAdapter(this);
        db.open();
        Cursor c = db.getRecord(id);
        EditText name = (EditText) findViewById(R.id.et_edit_name);
        RatingBar valuation = (RatingBar) findViewById(R.id.rb_edit_valuation);
        name.setText(c.getString(1));
        valuation.setRating(Float.parseFloat(c.getString(2)));
        c.close();
    }

    /**
     * Updates information of the selected item
     */
    public void editData(View view) {
        DBAdapter db = new DBAdapter(this);
        db.open();

        EditText name = (EditText) findViewById(R.id.et_edit_name);
        RatingBar valuation = (RatingBar) findViewById(R.id.rb_edit_valuation);

        String newName = name.getText().toString();
        String newValuation = String.valueOf(valuation.getRating());
        if (db.updateRecord(id_edit, newName, newValuation)) {
            Toast.makeText(this, "Elemento modificado", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }

    /**
     * User canceled the edit
     */
    public void noChanges(View view) {
        super.onBackPressed();
    }
}
