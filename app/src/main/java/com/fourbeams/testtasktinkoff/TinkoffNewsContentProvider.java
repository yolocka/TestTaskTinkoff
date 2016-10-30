package com.fourbeams.testtasktinkoff;

import android.content.*;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Provider stores news from Tinkoff server.
 * <br/>Consists of SQLite database {@value #DATABASE_NAME} with 2 tables:
 * <br/> {@value #NEWS_ITEMS_TABLE} stores list of news and {@value #NEWS_ITEM_CONTENT} stores content of each news
 * <br/>including such fields, as: {@value #TEXT} for storing news headers, {@value #ITEM_CONTENT} holding news bodies
 * <p/>
 * Provider could be accessed through the following URIs:
 * <br/> content://{@value #PROVIDER_NAME}...
 * <br/> <i>/newsList</i> - provides full list of news headers
 * <br/> <i>/newsList/newsListItem</i> - provides particular news header for the given item id
 * <br/> <i>/newsItemContent</i> - provides particular news body
 * <p/>
 * Method {@link #query} holds side effect of registering to watch a content URI for changes.
 * <br/> {@link #delete}, {@link #update}, {@link #insert} methods notifies observers when content changed.
 */
@SuppressWarnings("ConstantConditions")
public class TinkoffNewsContentProvider extends ContentProvider {

    public static final String PROVIDER_NAME = "com.fourbeams.testtasktinkoff.TinkoffNewsContentProvider";
    private static final String URL = "content://" + PROVIDER_NAME;
    public static final Uri CONTENT_URI_NEWS_LIST = Uri.parse(URL + "/newsList");
    public static final Uri CONTENT_URI_NEWS_LIST_ITEM = Uri.parse(URL + "/newsList/newsListItem");
    public static final Uri CONTENT_URI_NEWS_ITEM_CONTENT = Uri.parse(URL + "/newsItemContent");

    //fields in DB
    public static final String _ID = "_id";
    public static final String ITEM_ID = "id";
    public static final String TEXT = "text";
    public static final String PUBLICATION_DATE = "publicationDate";
    public static final String ITEM_CONTENT = "item_content";
    public static final String LAST_MODIFICATION_DATE = "lastModificationDate";

    //projection
    public static final String[] NEWS_LIST_PROJECTION = new String[]{
        TinkoffNewsContentProvider._ID,
        TinkoffNewsContentProvider.PUBLICATION_DATE,
        TinkoffNewsContentProvider.TEXT,
        TinkoffNewsContentProvider.ITEM_ID
    };
    public static final String[] NEWS_ITEM_CONTENT_PROJECTION = new String[]{
            TinkoffNewsContentProvider._ID,
            TinkoffNewsContentProvider.ITEM_CONTENT,
            TinkoffNewsContentProvider.LAST_MODIFICATION_DATE,
            TinkoffNewsContentProvider.ITEM_ID
    };

    //data URIs
    private static final int NEWS_LIST = 1;
    private static final int NEWS_LIST_ITEM = 2;
    private static final int NEWS_ITEM_CONTENT = 3;

    private static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "newsList", NEWS_LIST);
        uriMatcher.addURI(PROVIDER_NAME, "newsList/newsListItem", NEWS_LIST_ITEM);
        uriMatcher.addURI(PROVIDER_NAME, "newsItemContent", NEWS_ITEM_CONTENT);
    }

    private SQLiteDatabase db;
    private static final String DATABASE_NAME = "TinkoffNewsDb";
    private static final String NEWS_LIST_TABLE = "TinkoffNews";
    private static final String NEWS_ITEMS_TABLE = "TinkoffNewsItems";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_NEWS_LIST_TABLE =
            " CREATE TABLE " + NEWS_LIST_TABLE +
             " ( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
             ITEM_ID + " STRING UNIQUE, " +
             TEXT + " STRING, " +
             PUBLICATION_DATE + " LONG); ";

    private static final String CREATE_NEWS_ITEMS_TABLE =
            " CREATE TABLE " + NEWS_ITEMS_TABLE +
            " ( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ITEM_ID + " STRING, " +
            ITEM_CONTENT + " STRING); ";


    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_NEWS_LIST_TABLE);
            db.execSQL(CREATE_NEWS_ITEMS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + NEWS_LIST_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + NEWS_ITEMS_TABLE);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return db != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor c;
        String query;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)){
            case NEWS_LIST:
                qb.setTables(NEWS_LIST_TABLE);
                c = qb.query(db, projection, null, null, null, null, sortOrder + " DESC");
                break;
            case NEWS_LIST_ITEM:
                qb.setTables(NEWS_LIST_TABLE);
                query = "SELECT * FROM " + NEWS_LIST_TABLE + " WHERE " + ITEM_ID + " LIKE " + selection;
                c = db.rawQuery(query,null);
                break;
            case NEWS_ITEM_CONTENT:
                qb.setTables(NEWS_ITEMS_TABLE);
                query = "SELECT * FROM " + NEWS_ITEMS_TABLE + " WHERE " + ITEM_ID + " LIKE " + selection;
                c = db.rawQuery(query,null);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        long rowID;
        Uri _uri = null;
        switch (uriMatcher.match(uri)){
            case NEWS_LIST:
                rowID = db.insert(NEWS_LIST_TABLE, "", contentValues);
                if (rowID > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_NEWS_LIST, rowID);
                }
                break;
            case NEWS_ITEM_CONTENT:
                rowID = db.insert(NEWS_ITEMS_TABLE, "", contentValues);
                if (rowID > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URI_NEWS_ITEM_CONTENT, rowID);
                }
                break;
            default:
                throw new SQLException("Failed to add a row " + uri);
        }
        // notify observers
        notifyChange(_uri);
        return _uri;
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] strings) {
        int count;
        switch (uriMatcher.match(uri)){
            case NEWS_LIST:
                count = db.delete(NEWS_LIST_TABLE, s, strings);
                break;
            case NEWS_ITEM_CONTENT:
                count = db.delete(NEWS_ITEMS_TABLE, s, strings);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        // notify observers
        notifyChange(uri);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        int count;
        switch (uriMatcher.match(uri)){
            case NEWS_LIST:
                count = db.update(NEWS_LIST_TABLE, contentValues, s, strings);
                break;
            case NEWS_ITEM_CONTENT:
                count = db.update(NEWS_ITEMS_TABLE, contentValues, s, strings);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        // notify observers
        notifyChange(uri);
        return count;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)){
            case NEWS_LIST:
                return "vnd.android.cursor.dir/vnd.com.fourbeams.testtasktinkoff.newsList";
            case NEWS_ITEM_CONTENT:
                return "vnd.android.cursor.dir/vnd.com.fourbeams.testtasktinkoff.newsItemContent";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    private void notifyChange(Uri uri) {
        //Notify registered observers that a row was updated
        getContext().getContentResolver().notifyChange(uri, null);
    }
}
