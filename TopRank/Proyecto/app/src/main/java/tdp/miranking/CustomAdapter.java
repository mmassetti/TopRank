package tdp.miranking;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

/**
 * Custom adapter used to project the cursor's data into a view
 *
 * @author Matias Massetti
 * @version Jan-2017
 */

class CustomAdapter extends ArrayAdapter<MyEntity> {

    /**
     * Local variables
     */

    private AppCompatActivity activity;
    private List<MyEntity> myEntityList;

    /**
     * Creates the adapter
     */
    CustomAdapter(AppCompatActivity context, int resource, List<MyEntity> entities) {
        super(context, resource, entities);
        this.activity = context;
        this.myEntityList = entities;
    }


    /**
     * Gets an item of the list
     */
    @Override
    public MyEntity getItem(int position) {
        return myEntityList.get(position);
    }

    /**
     * Used to recycle the views of the list view
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ratingBar.setTag(position);
        holder.ratingBar.setRating(getItem(position).getValuation());
        holder.name.setText(getItem(position).getName());

        return convertView;
    }


    /**
     * Auxiliary class for this specific domain
     */
    private static class ViewHolder {
        private RatingBar ratingBar;
        private TextView name;

        ViewHolder(View view) {
            ratingBar = (RatingBar) view.findViewById(R.id.rate_img);
            name = (TextView) view.findViewById(R.id.text);
        }
    }


}



