package com.example.steven.popularmovies.Data;

/**
 * Created by Steven on 26/02/2018.
 */

// based on
// http://www.jameselsey.co.uk/blogs/techblog/extracting-out-your-asynctasks-into-separate-classes-makes-your-code-cleaner/

public interface AsyncTaskCompleteListener<T> {

    /**
     * Invoked when the AsyncTask has completed its execution.
     * @param result The resulting object from the AsyncTask.
     */
    void onTaskComplete(T result);

    void showLoadingIndicator();

    void hideLoadingIndicator();

    void showErrorMessage();
}
