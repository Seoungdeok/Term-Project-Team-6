package edu.uiuc.cs427app.MainActivity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import edu.uiuc.cs427app.Models.City;
import edu.uiuc.cs427app.R;

/**
 * This class is a bridge between the data stored in the {@link edu.uiuc.cs427app.Models.City}
 * object and the UI elements that will display the city. For more information about why this
 * class is needed and what it does see
 * {@link <a href="https://developer.android.com/develop/ui/views/layout/recyclerview#java">Recycler View Android Documentation</a>}
 *
 * @author Kyr Nastahunin
 */
public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.CityListViewHolder> {
    private List<City> cities; // stores the cities to be displayed
    private RVClickListener RVlistener; //listener defined in main activity
    private RVLongClickListener RVDeleteListener; //delete listener defined in main activity

    public CityListAdapter(List<City> cities, RVClickListener RVlistener, RVLongClickListener RVDeleteListener) {
        this.cities = cities;
        this.RVlistener = RVlistener;
        this.RVDeleteListener = RVDeleteListener;
    }

    /**
     * {@link RecyclerView} calls this method whenever it needs to create a new {@link CityListViewHolder}.
     * The method creates and initializes the {@link CityListViewHolder} and its associated View,
     * but does not fill in the view's contents—the {@link CityListViewHolder} has not yet been bound to
     * specific data.
     */
    @NonNull
    @Override
    public CityListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate view for individual list/grid item defined in XML spec
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View listView = inflater.inflate(R.layout.city_list_item, parent, false);
        CityListViewHolder viewHolder = new CityListViewHolder(listView, RVlistener, RVDeleteListener);
        return viewHolder;
    }

    /**
     * {@link RecyclerView} calls this method to associate a {@link CityListViewHolder} with data.
     * The method fetches the appropriate data and uses the data to fill in the view holder's
     * layout. For example, if the {@link RecyclerView} displays a list of names, the method might find
     * the appropriate name in the list and fill in the view holder's
     *
     * @param holder   A reference to the object that represents an individual item on the list
     *                 from which the data will be displayed.
     * @param position An index of the item that is being added to the list.
     */
    @Override
    public void onBindViewHolder(@NonNull CityListViewHolder holder, int position) {
        // connects an individual string in the List to it's UI element
        holder.cityName.setText(cities.get(position).getCityName());
        holder.cityCountry.setText(cities.get(position).getCityCountry());

        // asynchronously download and display the image
        Glide.with(holder.image.getContext())
                .load(cities.get(position).getPictureURL()) // which image to download
                .listener(new RequestListener<Drawable>() { // define what happens after image was loaded
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE); // remove the progress bar
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE); // remove the progress bar
                        return false;
                    }
                })
                .into(holder.image); // load into which ImageView
    }

    //RecyclerView needs this for item generation
    @Override
    public int getItemCount() {
        if (cities.size() > 10) {
            return 10;
        }
        return cities.size();
    }


    /**
     * This class represents a UI item in our {@link RecyclerView}
     *
     * @author Kyr Nastahunin
     */
    public class CityListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        //Fields to be populated
        public TextView cityName;
        public TextView cityCountry;
        public ImageView image;
        public ProgressBar progressBar; // used when image is loading
        private RVClickListener listener;
        private RVLongClickListener deleteListener;

        /**
         * Constructor for the ViewHolder. It is used to find the individual views in the xml file
         * and associate them with their instance in code.
         *
         * @param itemView       A reference to the compiled layout file (city_list_item)
         * @param passedListener
         */
        public CityListViewHolder(@NonNull View itemView, RVClickListener passedListener, RVLongClickListener passedDeleteListener) {
            super(itemView);
            // find the individual views and save them
            cityName = (TextView) itemView.findViewById(R.id.cityName);
            cityCountry = (TextView) itemView.findViewById(R.id.cityCountry);
            image = (ImageView) itemView.findViewById(R.id.imageView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            // use a premade listener that includes the index of item that was clicked
            this.listener = passedListener;
            this.deleteListener = passedDeleteListener;
            itemView.setOnClickListener(this); //set short click listener
            itemView.setOnCreateContextMenuListener(this);
        }

        /**
         * What happens when this item in the list is clicked
         *
         * @param v A reference to the UI item that was clicked
         */
        @Override
        public void onClick(View v) {
            // call the onClick method in the predefined listener and include the index of this item
            listener.onClick(v, getAdapterPosition());
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            // inflate the context menu that pops up due to long click
            MenuInflater inflater = new MenuInflater(view.getContext());
            inflater.inflate(R.menu.city_item_context_menu, contextMenu);
            contextMenu.getItem(0).setOnMenuItemClickListener(onMenuOpen);
            contextMenu.getItem(1).setOnMenuItemClickListener(onMenuDelete);
        }

        // listener for context menu Open button press
        private final MenuItem.OnMenuItemClickListener onMenuOpen = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                listener.onClick(null, getAdapterPosition());
                return false;
            }
        };

        // listener for context menu Delete button press
        private final MenuItem.OnMenuItemClickListener onMenuDelete = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                deleteListener.onClick(menuItem, getAdapterPosition());
                return false;
            }
        };
    }
}
