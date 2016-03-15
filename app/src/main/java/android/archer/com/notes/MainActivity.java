package android.archer.com.notes;

import android.app.ListActivity;
import android.archer.com.notes.db.NotesDB;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends ListActivity {

    private View.OnClickListener btnAddNote_clickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivityForResult(new Intent(MainActivity.this, AtyEditNotes.class), REQUEST_CODE_ADD_NOTE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new SimpleCursorAdapter(this, R.layout.notes_list_cell, null, new String[]{NotesDB.COLUMN_NAME_NOTE_NAME, NotesDB.COLUMN_NAME_NOTE_DATE}, new int[]{R.id.tvName, R.id.tvDate});
        setListAdapter(adapter);

        NotesDB db = new NotesDB(this);
        dbRead = db.getReadableDatabase();
        refreshNotesListView();


        findViewById(R.id.btnAddNote).setOnClickListener(btnAddNote_clickHandler);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        Cursor c=adapter.getCursor();
        c.moveToPosition(position);

        Intent i=new Intent(MainActivity.this,AtyEditNotes.class);
        i.putExtra(AtyEditNotes.EXTRA_NOTE_ID,c.getInt(c.getColumnIndex(NotesDB.COLUMN_NAME_ID)));
        i.putExtra(AtyEditNotes.EXTRA_NOTE_NAME,c.getString(c.getColumnIndex(NotesDB.COLUMN_NAME_NOTE_NAME)));
        i.putExtra(AtyEditNotes.EXTRA_NOTE_Content,c.getString(c.getColumnIndex(NotesDB.COLUMN_NAME_NOTE_CONTENT)));

        startActivityForResult(i, REQUEST_CODE_EDIT_NOTE);


        super.onListItemClick(l, v, position, id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_ADD_NOTE:
            case REQUEST_CODE_EDIT_NOTE:
                if (resultCode == MainActivity.RESULT_OK) {
                    refreshNotesListView();
                }
                break;
            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //刷新列表
    public void refreshNotesListView() {

        adapter.changeCursor(dbRead.query(NotesDB.TABLE_NAME_NOTES, null, null, null, null, null, null));

    }

    private SimpleCursorAdapter adapter = null;
    private SQLiteDatabase dbRead;
    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_EDIT_NOTE = 2;



}
