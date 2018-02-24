# POPULAR MOVIES

### ABOUT 
This Android application is part of the Udacity's **Android Developer Nanodegree**.
The objective is to use an API provided by https://www.themoviedb.org/ to make a call to get a list of movies 
and show the result on the screen. Depending on the user preferences, a different call has to be made.
By clicking on one of the movies, a detailed view is shown.

### NOTE
In order for the application to work correctly, an API key needs to be requested on 
https://developers.themoviedb.org/3/getting-started/introduction and filled in in the 
NetworkUtils.java class
```
public static final String API_KEY_VALUE = "";
```

### WHAT I LEARNED
Important things I learned doing this project include:
- Performing an HTTP request on a background thread
- Parsing JSON response and fill UI with the data
- Checking for internet connection before starting the API call
- Settings fragment with SharedPreferences
- Use of ConstraintLayout for UI
- ...
