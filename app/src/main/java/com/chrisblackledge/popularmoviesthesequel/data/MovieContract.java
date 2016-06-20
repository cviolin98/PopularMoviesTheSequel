package com.chrisblackledge.popularmoviesthesequel.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.chrisblackledge.popularmoviesthesequel.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class MovieEntry implements BaseColumns {

        // table names
        public static final String TABLE_FAVORITES = "favorites";

        // table columns
        public static final String _ID = "_id";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_USER_RATING = "user_rating";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_OVERVIEW = "overview";

        // table column indices
        public static final int _ID_INDEX = 0;
        public static final int COLUMN_MOVIE_ID_INDEX = 1;
        public static final int COLUMN_RELEASE_DATE_INDEX = 2;
        public static final int COLUMN_USER_RATING_INDEX = 3;
        public static final int COLUMN_POSTER_INDEX = 4;
        public static final int COLUMN_ORIGINAL_TITLE_INDEX = 5;
        public static final int COLUMN_OVERVIEW_INDEX = 6;

        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_FAVORITES).build();

        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_FAVORITES;

        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_FAVORITES;

        // for building URIs on insertion
        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
