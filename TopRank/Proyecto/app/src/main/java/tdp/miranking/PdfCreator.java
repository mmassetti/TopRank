package tdp.miranking;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Class used to create a PDF file with the elements of the database
 *
 * @author Matias Massetti
 * @version Jan-2017
 */
public class PdfCreator {

    /**
     * Local variables
     */

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Context context;
    private File myFile;
    private File pdfFolder;
    private Document document;
    private PdfPCell cell_1;
    private PdfPCell cell_2;
    private PdfPTable table;
    private Font itemFont;
    private BaseColor colorCell;

    /**
     * Instantiates the class, getting the context and a cursor to the elements of the database
     */
    public PdfCreator(Context ctx) {
        context = ctx;
    }


    /**
     * Creates the PDF file with the database information
     */
    public void createPdf() {
        checkPermissions((Activity) context);

        try {
            prepareDocument();
            writeDocument();

            //Show document
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(myFile), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            context.startActivity(intent);

        } catch (FileNotFoundException | DocumentException e) {
            e.printStackTrace();
        }

    }


    /**
     * Checks the permissions needed to create the PDF
     */
    private void checkPermissions(Activity activity) {
        //Check permission to write
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }

        //Check external storage permission
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Toast.makeText(context, "Error-No se puede crear el PDF", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Sets document structure and properties
     */
    private void prepareDocument() throws FileNotFoundException, DocumentException {
        //Check the creation folder
        pdfFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/MiRankingPDFs");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
        }

        //Creates a record of the current date
        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(date);

        //Creates the PDF file
        myFile = new File(pdfFolder + timeStamp + ".pdf");
        OutputStream output = new FileOutputStream(myFile);

        //Begins use of iText library...

        //Step 1: Creates a new document
        document = new Document();

        //Step 2: Preparation to write into the document
        PdfWriter.getInstance(document, output);

        //Step 3: Open the document
        document.open();

        //Step 4: Creates a paragraph and add it to the document

        Paragraph p = new Paragraph();
        document.add(p);

        //Step 5: Creates a table and sets its properties...
        table = new PdfPTable(2);
        table.setWidthPercentage(100);

        //Fonts and colors used
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD);
        titleFont.setColor(255, 255, 255);
        itemFont = new Font(Font.FontFamily.HELVETICA, 18, Font.NORMAL);
        itemFont.setColor(255, 255, 255);
        colorCell = new BaseColor(30, 144, 255);

        //Name cell
        cell_1 = new PdfPCell(new Phrase("NOMBRE", titleFont));
        cell_1.setBackgroundColor(colorCell);
        cell_1.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell_1.setFixedHeight(50);

        //Valuation cell
        cell_2 = new PdfPCell(new Phrase("VALORACIÓN", titleFont));
        cell_2.setBackgroundColor(colorCell);
        cell_2.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell_2.setFixedHeight(50);

        //Adding cells into the table
        table.addCell(cell_1);
        table.addCell(cell_2);
    }


    /**
     * Puts database information into the document
     */
    private void writeDocument() throws DocumentException {
        DBAdapter db = new DBAdapter(context);
        db.open();
        Cursor c = db.getAllRecords();

        while (c.moveToNext()) {
            cell_1 = new PdfPCell(new Phrase(c.getString(1).toUpperCase(), itemFont));
            cell_1.setBackgroundColor(colorCell);
            cell_1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell_1.setFixedHeight(50);

            cell_2 = new PdfPCell(new Phrase(c.getString(2), itemFont));
            cell_2.setBackgroundColor(colorCell);
            cell_2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell_2.setFixedHeight(50);

            table.addCell(cell_1);
            table.addCell(cell_2);
        }

        document.add(table);
        document.addCreationDate();
        document.close();
    }
}

