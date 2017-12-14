package tdp.miranking;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * This activity is used to choose between adding, editing or deleting an item.
 * It also shows all the items and provides a search filter.
 *
 * @author Matias Massetti
 * @version Jan-2017
 */

public class MainActivity extends AppCompatActivity {

    /**
     * Local variables
     */

    private static int bestToWorst = 0;
    private static int worstToBest = 0;
    private static int searching = 0;
    private static String searchTerm = "";
    private String[] arrayIds;
    private ListView list;
    private SearchView sv;
    private Button btn_add;
    private Button btn_edit;
    private Button btn_delete;
    private ImageView closeButton;
    private CustomAdapter adapter;
    private Toolbar toolbar;
    private ArrayList<MyEntity> entities = new ArrayList<>();
    private int selectedItem = -1;

    /**
     * Called when the activity is starting
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getInfo();
        setInfo();
        adapter = new CustomAdapter(this, R.layout.list_item, entities);
        setListeners();
    }

    /**
     * Gets the attributes of the layout
     */

    private void getInfo() {
        int searchCloseButtonId;
        btn_add = (Button) findViewById(R.id.btn_agregar);
        btn_delete = (Button) findViewById(R.id.btn_eliminar);
        btn_delete.setEnabled(false);
        btn_edit = (Button) findViewById(R.id.btn_editar);
        btn_edit.setEnabled(false);
        list = (ListView) findViewById(R.id.lv_lista);
        sv = (SearchView) findViewById(R.id.sv_buscar);
        searchCloseButtonId = sv.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        closeButton = (ImageView) this.sv.findViewById(searchCloseButtonId);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
    }

    /**
     * Sets properties of the toolbar.
     */
    private void setInfo() {
        setSupportActionBar(toolbar);
        String title = "TopRank";
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
        toolbar.setLogo(R.drawable.ic_toolbar_icon2);
    }

    /**
     * Sets listeners for layout components
     */
    private void setListeners() {
        // Listener list of items
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(0100);
                selectedItem = position;
                btn_delete.setEnabled(true);
                btn_edit.setEnabled(true);
                view.setSelected(true);
            }
        });

        // Listener add button
        btn_add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                add();
            }
        });

        // Listener edit button
        btn_edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                edit();
            }
        });

        // Listener delete button
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });

        // Listener search view
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                sv.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searching = 1;
                searchTerm = newText;
                refresh();
                return false;
            }

        });

        // Listener close button of the search view
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sv.setQuery("", false);
                sv.clearFocus();
                refresh();
                searching = 0;
            }
        });

    }

    /**
     * Changes activity to Add activity
     */
    protected void add() {
        Intent intent = new Intent(MainActivity.this, AddItem.class);
        startActivity(intent);
    }

    /**
     * Changes activity to Edit activity
     */
    protected void edit() {
        btn_edit.setEnabled(false);
        btn_delete.setEnabled(false);
        int idEdit = Integer.parseInt(arrayIds[selectedItem].split(" ")[0]);
        Intent intent = new Intent(MainActivity.this, EditItem.class);
        intent.putExtra("idToEdit", idEdit);
        startActivity(intent);
    }

    /**
     * Deletes the selected item of the database
     */
    protected void delete() {
        btn_delete.setEnabled(false);
        btn_edit.setEnabled(false);
        int id = Integer.parseInt(arrayIds[selectedItem].split(" ")[0]);
        AlertDialog diaBox = confirmDeleteItemOption(id);
        diaBox.show();
    }

    /**
     * Deletes all the items of the database
     */
    protected void deleteDB() {
        DBAdapter db = new DBAdapter(this);
        db.deleteDataBase();
        list.setAdapter(null);
    }

    /**
     * Refreshes the list of items
     */
    protected void refresh() {
        DBAdapter db = new DBAdapter(this);
        db.open();
        Cursor c;
        if (searching == 1) {
            c = db.retrieve(searchTerm);
        } else if (bestToWorst == 1) {
            c = db.sortBestToWorst();
        } else if (worstToBest == 1) {
            c = db.sortWorstToBest();
        } else {
            c = db.getAllRecords();
        }
        refreshList(c);
        db.close();
        adapter = new CustomAdapter(this, R.layout.list_item, entities);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * Auxiliary method to refresh the list
     */
    protected void refreshList(Cursor c) {
        int size = c.getCount();
        entities = new ArrayList<>(size);
        arrayIds = new String[size];
        int i = 0;
        if (c.moveToFirst()) {
            do {
                arrayIds[i] = c.getString(0);
                entities.add(new MyEntity(c.getString(1).toUpperCase(), c.getFloat(2)));
                i++;
            } while (c.moveToNext());
        }
    }

    @Override
    /**
     * Returns to activity
     */
    protected void onResume() {
        super.onResume();
        refresh();
    }

    /**
     * Displays the options menu
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    /**
     *  Manages the options menu
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                sortBestToWorst();
                return true;
            case 2:
                sortWorstToBest();
                return true;
            case 3:
                sortDefault();
                return true;
            case R.id.eliminar_todo:
                AlertDialog diaBox = confirmDeleteAllOption();
                diaBox.show();
                return true;
            case R.id.ver_pdf:
                createPDF();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Displays the list of items in order of entry
     */
    protected void sortDefault() {
        bestToWorst = 0;
        worstToBest = 0;
        refresh();
    }

    /**
     * Displays the list of items from highest to lowest valuation
     */
    protected void sortBestToWorst() {
        bestToWorst = 1;
        worstToBest = 0;
        refresh();
    }

    /**
     * Displays the list of items from lowest to highest valuation
     */
    protected void sortWorstToBest() {
        bestToWorst = 0;
        worstToBest = 1;
        refresh();
    }

    /**
     * Creates a PDF document and show it
     */
    protected void createPDF() {
        PdfCreator pdfCreator = new PdfCreator(MainActivity.this);
        pdfCreator.createPdf();
    }


    /**
     * Confirm before deleting all items in the database
     */
    protected AlertDialog confirmDeleteAllOption() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Eliminar todo")
                .setMessage("¿Está seguro?")

                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteDB();
                        dialog.dismiss();
                    }
                })

                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;

    }

    /**
     * Confirm before deleting a specific item in the database
     */
    protected AlertDialog confirmDeleteItemOption(int i) {
        final int id = i;
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                .setTitle("Eliminar elemento")
                .setMessage("¿Está seguro?")

                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DBAdapter db = new DBAdapter(getApplicationContext());
                        db.open();
                        if (!db.deleteRecord(id))
                            Toast.makeText(getApplicationContext(), "Error - No se pudo eliminar.", Toast.LENGTH_SHORT).show();
                        db.close();
                        refresh();
                        dialog.dismiss();
                    }
                })

                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;

    }


}

