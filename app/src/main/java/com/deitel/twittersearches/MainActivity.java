package com.deitel.twittersearches;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextWatcher;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //The static String constant SEARCHES represents the name of a SharedPreferences file
    //that will store tag-query pairs on the device.
    private static final String SEARCHES = "searches";

    //EditText where user enters a query
    private EditText queryEditText;
    //EditText where user enters a query's tag.
    private EditText tagEditText;
    //FAB that user touches to save a search.
    private FloatingActionButton saveFloatingActionButton;

    //SharedPreferences instance variable which we'll use to manipulate the tag-query pairs
    //representing the user's saved searches.
    private SharedPreferences savedSearches;

    //The List that will store the sorted tag names for user searches.
    private List<String> tags;

    //SearchesAdapter instance variable which will refer to RecyclerView.Adapter subclass object
    //that provides data to the RecyclerView.
    private SearchesAdapter adapter;



    //onCreate will configure the GUI and register event listeners.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //First we must get references to the EditTexts and add TextWatchers to them.
        //TextWatchers are notified when the user enters or removes characters in the EditTexts.
        queryEditText = ((TextInputLayout) findViewById(R.id.queryTextInputLayout)).getEditText();
        queryEditText.addTextChangedListener(textWatcher);
        tagEditText = ((TextInputLayout) findViewById(R.id.tagTextInputLayout)).getEditText();
        tagEditText.addTextChangedListener(textWatcher);



        //We use the getSharedPreferences method to get a SharedPreference object that can read
        //existing tag-query pairs from the searches file.
        //The first argument indicates the name of the file that contains the data.
        //The second arg specifies the file's access-level.
        savedSearches = getSharedPreferences(SEARCHES, MODE_PRIVATE);

        //We want to display the search tags alphabetically

        //First we get the Strings representing the keys in the SharedPreferences object
        //and store them in our array "tags". The getAll method returns all the saved searches
        //as a Map - a collection of key-value pairs. We then call the keySet method on the Map object
        //to get all the keys as a Set - a collection of unique values.
        //The result is used to initialize "tags".
        tags = new ArrayList<>(savedSearches.getAll().keySet());
        //We then call Collections.sort to sort our ArrayList.
        Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);

        //Now we must configure our RecyclerView

        //First we get a reference to it.
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);


        //A RecyclerView can arrange items in multiple ways. We use a LinearLayoutManager
        //to display the item in a vertical list. LLM's constructor receives a Context object,
        //which is the MainActivity in this case.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Then we create a SearchesAdapter that will supply data for display in the RecyclerView.
        adapter = new SearchesAdapter(tags, itemClickListener, itemLongClickListener);
        recyclerView.setAdapter(adapter);

        //Lastly we create an ItemDivider object and pass it to addItemDecoration, which enables
        //RecyclerView to draw a horizontal line decoration between list items.
        recyclerView.addItemDecoration(new ItemDivider(this));

        //Now we must register a listener for FAB

        //First we get a reference to our FAB
        saveFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        //Then we register its OnClickListener using instance variable saveButtonListener.
        //saveButtonListener refers to an anon-inner-class object that implements
        //the interface View.OnClickListener.
        saveFloatingActionButton.setOnClickListener(saveButtonListener);


        //We call updateSaveFAB which initially hides the FAB button bc the EditTexts
        //are empty when onCreate is first called. The button is displayed only when both
        //EditTexts contain input.
        updateSaveFAB();
    }





    //textWatcher is an anon-inner-class that implements interface TextWatcher.
    //Its onTextChanged method calls updateSaveFAB when the content changes in either
    //of the app's EditTexts.
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }


        //hide/show the FAB after user changes input
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            updateSaveFAB();

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };




    //the updateSaveFAB method checks whether theres texts in both EditTexts.
    private void updateSaveFAB(){

        //If both or either of the EditTexts are empty, we call the FAB's hide method to hide the button. Because both
        //the query and tag are required before a tag-query pair can be saved.
        if (queryEditText.getText().toString().isEmpty() || tagEditText.getText().toString().isEmpty())
            saveFloatingActionButton.hide();
        else
            //If they both contain text then we call the FAB's show method. So the user can touch it
            //and store a query-tag pair.
            saveFloatingActionButton.show();
    }


    //Here we define the instance variable saveButtonListener, which is an anon-inner-class
    //object implements the interface OnClickListener
    private final OnClickListener saveButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {


            //First we get the Strings from the EditTexts
            String query = queryEditText.getText().toString();
            String tag = tagEditText.getText().toString();


            //If the user entered a query and a tag then
            if (!query.isEmpty() && !tag.isEmpty()){

                //hide the soft keyboard
                ((InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);

                //call the addTaggedSearch method to store the tag-query pair.
                //The addTaggedSearch method we created adds a new search to savedSearches
                //or modifies an existing search.
                addTaggedSearch(tag, query);

                //clear the two EditTexts
                queryEditText.setText("");
                tagEditText.setText("");

                //call the EditText's requestFocus method to position the input curson in the queryEditText
                queryEditText.requestFocus();
            }
        }
    };


    //The addTaggedSearch method adds a new search to savedSearches.
    //It adds a new search to file, then refreshes all buttons.
    private void addTaggedSearch(String tag, String query){

        //To change a SharedPreferences object you must first get its editor. The Editor can
        //add , remove, and modify the values associated with a particular key in SP file.
        //We get a SharedPreferences.Editor to store a new tag/query pair.
        SharedPreferences.Editor preferencesEditor = savedSearches.edit();

        //Then we call Editor's putString() method to save the search's tag (the key) and query (the value).
        //If the tag already exists the value is updated.
        preferencesEditor.putString(tag, query);
        //Lastly we call Editor's apply method to commit the changes.
        preferencesEditor.apply();

        //Finally we have to notify the RecyclerView.Adapter that its data has changed.
        //When the user adds a new search, the RecyclerView should be updated to display it.

        //We check if a new tag was added.
        if (!tags.contains(tag)){
            //if so then we add the new search's to tags and sort it.
            tags.add(tag);
            Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);
            //then call notifyDataSetChanged to indicate that the underlying data in "tags" has changed.
            //the RecyclerView.Adapter then notifies RecyclerView to update its lost of displayed items.
            adapter.notifyDataSetChanged();
        }
    }



    //Here we define an instance variable itemClickListener, which refers to an anon-inner-class object
    //that implements the OnClickListener interface.
    //The view registered with this OnClickListener will be the TextView that displays a search tag in RecyclerView.
    private final OnClickListener itemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            //First we get the Text of the View touched. This is the tag for a search.
            String tag = ((TextView) v).getText().toString();



            //Then we create a String containing the Twitter Search URL and the query to perform.
            //We append the the TwitterSearchURL with the query using savedSearches, which is our SharedPreferences
            //getString method which returns the query associated with the tag. If the tag doesnt already exist then
            //the second argument ("") is returned. We then pass the query to Uri's encode method which escapes any
            //special URL characters such as (?,/,;etc) and returns a so-called URL-encoded string. Class Uri enables
            //us to convert a URL into the format required by an Intent that launches the device's browser. This is important
            //to ensure Twitter webserver receives a request properly parsed.
            String urlString = getString(R.string.search_URL) + Uri.encode(savedSearches.getString(tag, ""), "UTF-8");




            //Then we create an Intent to launch the web browser. Here we use an implicit intent.
            //The 1st arg of Intents constructor is a constant describing the action to perform. We specify ACTION_VIEW
            //to indicae we want to display a representation of the Intent's data.
            //The 2nd arg is a Uri representing the data for which to perform the action. Class Uri's parse method
            //converts a String representing a URL to a URI.
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));

            //Show results in web browser.
            startActivity(webIntent);

        }
    };








    //Here we define an instance variable itemLongClickListener which refers to an anon-inner-class object
    //that implements the interface OnLongClickListener.
    //The view registered with this OnLongClickListener will be the TextView that displays search tags.
    //It allows the user to share, edit, or delete a saved search.
    private final OnLongClickListener itemLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {

            //First assign a final local variable tag to the text of the item the user long pressed.
            //"final" is required for any local variable or method parameter used in an anon-inner-class.
            final String tag = ((TextView) v).getText().toString();

            //Next we create an AlertDialog.Builder to start building our AlertDialog box.
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            //We set the AlertDialog's title to a formatted String. With a tag that represents
            //the format specifier. We use the getString method as it can receive multiple arguments and we use it
            //to specify a String resource ID representing a format String and the values that should replace format specifiers.
            builder.setTitle(getString(R.string.share_edit_delete_title, tag));

            //In addition to buttons, an AlertDialog can display a list of items. We use the builder's setItems's method
            //to display the array of Strings R.array.dialog_items (Share,Edit,Delete)
            //and we define an anon-inner-class object that responds when the user touches any item in the list.
            builder.setItems(R.array.dialog_items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    switch (which){
                        case 0: //user clicks Share
                            shareSearch(tag);
                            break;
                        case 1: //user clicks Edit
                            //display the search'query and tag in the EditTexts
                            tagEditText.setText(tag);
                            queryEditText.setText(savedSearches.getString(tag, ""));
                            break;
                        case 2://user clicks Delete
                            deleteSearch(tag);
                            break;
                    }
                }
            });


            //Lastly we configure the AlertDialog's negative button. When the negative buttons event handler
            //is null touching the neg button simply dismisses the dialog.
            builder.setNegativeButton(getString(R.string.cancel), null);
            builder.create().show(); //create and show the AlertDialog.
            return true;
        }
    };



    //Method shareSearch is called when the user selects to share a search.
    private void shareSearch(String tag){

        //First we create a String representing the search to share.
        String urlString = getString(R.string.search_URL) + Uri.encode(savedSearches.getString(tag, ""), "UTF-8");

        //Then we create and configure an Intent that allows the user to send the search URL using an Activity
        //that can handle the INTENT.ACTION_SEND
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);

        //An intent includes a Bundle of extras - additional info that can be passed to the Activity that handles the Intent.
        //We use putExtra method to add to Intent's Bundle key-value pairs representing the extras.
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message, urlString));
        //Lastly we set the Intent's MIME type to text/plain-such data can be handled by any Activity capable
        //of sending plain text messages.
        shareIntent.setType("text/plain");

        //Finally we pass the Intent and a String title to Intent's createChooser method to display an intent chooser.
        //Its important to set a title here to remind the user to select an appropriate activity.
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_search)));

    }



    //The deleteSearch method is called when the user long presses a search tag and selects Delete from the AlertDialog.
    //Before deleting the search, the app displays an AlertDialog to confirm the delete operation.
    private void deleteSearch(final String tag){

        //First we create an AlertDialog.Builder to start building our AlertDialog.
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);

        //We set its message with our formatted String and format specifier.
        confirmBuilder.setMessage(getString(R.string.confirm_message, tag));

        //Then we config its negative (CANCEL) button to dismiss the AlertDialog if clicked.
        confirmBuilder.setNegativeButton(getString(R.string.cancel), null);

        //Then we config its positive (DELETE) button to remove the search.
        confirmBuilder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //We remove the tag from the "tags" List.
                tags.remove(tag);
                //Then we remove it from SharedPreferences, for which we must first get a SP.Editor object.
                SharedPreferences.Editor preferencesEditor = savedSearches.edit();
                preferencesEditor.remove(tag); //remove from SP.
                preferencesEditor.apply();     //commit the change.
                //notify the Adapter the underlying data has changed so it can update itself.
                adapter.notifyDataSetChanged();
            }
        });

        //Finally create and show the AlertDialog.
        confirmBuilder.create().show();
    }

}
