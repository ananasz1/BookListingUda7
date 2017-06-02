package com.example.schmidegv.booklistinguda7;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<Book>> {

    private final static String BASE_URL = "https://www.googleapis.com/books/v1/volumes?maxResults=40&q=";
    private String mUrl;
    /**
     * Constant value for the book loader ID. We can choose any integer.
     */
    private static final int BOOK_LOADER_ID = 1;
    /**
     * Adapter for the list of books
     */
    private BookAdapter mAdapter;
    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;
    private ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Find a reference to the in the activity_main
        final EditText searchTextView = (EditText) findViewById(R.id.text_search);
        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        ListView bookListView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);


        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        bookListView.setEmptyView(mEmptyStateTextView);

        loadingIndicator.setVisibility(View.GONE);
        mEmptyStateTextView.setText(R.string.question);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);
        // get loader only if user has searched something
        if (savedInstanceState != null) {
            mUrl = savedInstanceState.getString("URL");
        }
        if (mUrl != null) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        }
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current book that was clicked on
                Book currentBook = mAdapter.getItem(position);
                // Convert the String URL into a URI object (to pass into the Intent constructor)

                if (currentBook != null) {
                    Uri bookUri = Uri.parse(currentBook.getUrl());
                    // Create a new intent to view the book URI
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                    // Send the intent to launch a new activity
                    startActivity(websiteIntent);
                }

            }
        });

        Button searchButton = (Button) findViewById(R.id.button_search);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //* get user input and set it ready for url
                String keyWord = searchTextView.getText().toString().trim();

                    keyWord = keyWord.replaceAll(" +", "+");
                    //* make url
                    mUrl = BASE_URL + keyWord;
                    //* make UI look for loading books
                    mAdapter.clear();
                    loadingIndicator.setVisibility(View.VISIBLE);
                    mEmptyStateTextView.setText("");
                    //*close keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    //* get book data from internet
                    getBooksDataInBackground();
                }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString("URL", mUrl);
    }

    private void getBooksDataInBackground() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.restartLoader(BOOK_LOADER_ID, null, this);
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new BookLoader(this, mUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {

        // Hide loading indicator because the data has been loaded
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No books found."
        mEmptyStateTextView.setText(R.string.no_result);

        // Clear the adapter of previous book data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);

        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
}
