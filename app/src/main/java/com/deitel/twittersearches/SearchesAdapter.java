package com.deitel.twittersearches;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Yevgeniy on 6/27/2017.
 */
//The SearchesAdapter class is a subclass of RecyclerView.Adapter and it defines how
//to bind the tag names for the user's searches to the RecyclerView's items.
//Class MainActivity's onCreate method creates an object of SearchAdapter as the
//RecyclerView's adapter.
public class SearchesAdapter extends RecyclerView.Adapter<SearchesAdapter.ViewHolder> {


    //Here we define instance variables which will maintain references to the event listeners (defined in MA)
    //that are registered for each RecyclerView item.
    private final View.OnClickListener clickListener;
    private final View.OnLongClickListener longClickListener;

    //The "tags" instance variable maintains a reference to MA's List<String> that contains the tag names to display.
    //It is used to obtain RecyclerView item's data
    private final List<String> tags;

    //constructor
    public SearchesAdapter(List<String> tags, View.OnClickListener clickListener, View.OnLongClickListener longClickListener){
        this.tags = tags;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }






    //Every item in a RecyclerView must be wrapped in its own ViewHolder.
    //For this app we define a Recycler.ViewHolder called "ViewHolder"
    public static class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView textView;

        //The ViewHolder constructor receives a View object and listens for that View's OnClick/OnLongClick events.
        //The View represents an item in the RecyclerView, which is passed to the superclass's constructor.
        public ViewHolder(View itemView, View.OnClickListener clickListener, View.OnLongClickListener longClickListener){

            super(itemView);
            //First we store a reference to the TextView for the item.
            textView = (TextView) itemView.findViewById(R.id.textView);

            //Then we register the TextView's OnClickListener which displays the search results for that TextView's tag.
            itemView.setOnClickListener(clickListener);
            //Then we register the TextView's OnLongClickListener, which opens the Share,Edit,Delete dialog
            //for that TextView's tag.
            itemView.setOnLongClickListener(longClickListener);
        }
    }






    //RecyclerView calls its Adapter's onCreateViewHolder method to inflate the layout for each
    //RecyclerView item and wrap it in an object of RecyclerView.ViewHolder subclass.
    //This new ViewHolder object is returned to the RecyclerView for display.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //inflate the list_item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);

        //create a new ViewHolder for current item.
        return (new ViewHolder(view, clickListener, longClickListener));
    }




    //The RecyclerView calls its Adapter's onBindViewHolder method to set the data thats displayed
    //for a particular RecyclerView item. The method receives:
    //1) an object of our custom subclass of RecyclerView.ViewHolder containing the Views in which data
    //will be displayed. In this case its one TextView.
    //2) an int representing the item's position in the RecyclerView.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //Here we set the TextView's text to the String in "tags" List at the given position.
        holder.textView.setText(tags.get(position));
    }




    //The RecyclerView calls its Adapter's getItemCount method to obtain the total number of items that the
    //RecyclerView needs to display. In our case it is the number of items in our "tags" List.
    @Override
    public int getItemCount() {
        return tags.size();
    }



}
