package android.archer.com.notes;

import android.app.ListActivity;
import android.archer.com.notes.db.NotesDB;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Archer on 2016/3/15.
 */
public class AtyEditNotes extends ListActivity {

    private View.OnClickListener btnClickHandler=new View.OnClickListener() {

        Intent i;
        File f;



        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnAddPhoto:
                    i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    f=new File(getMediaDir(),System.currentTimeMillis()+".jpg");
                    if (f.exists()){
                        try {
                            f.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    currentPath=f.getAbsolutePath();

                    i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                     startActivityForResult(i, REQUEST_CODE_GET_PHOTO);

                    break;
                case R.id.btnAddVideo:
                    i=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    f=new File(getMediaDir(),System.currentTimeMillis()+".mp4");
                    if (f.exists()){
                        try {
                            f.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    currentPath=f.getAbsolutePath();

                    i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(i,REQUEST_CODE_GET_VIDEO);

                    break;
                case R.id.btnSave:
                    saveMedia(saveNote());
                    setResult(RESULT_OK);
                    finish();
                    break;
                case R.id.btnCancel:
                    setResult(RESULT_CANCELED);
                    finish();
                    break;

                default:

                    break;
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.aty_edit_note);

        db=new NotesDB(this);
        dbRead=db.getReadableDatabase();
        dbWrite=db.getWritableDatabase();

        adapter=new MediaAdapter(this);
        setListAdapter(adapter);


        etName= (EditText) findViewById(R.id.etName);
        etContent= (EditText) findViewById(R.id.etContent);

        noteId =getIntent().getIntExtra(EXTRA_NOTE_ID,-1);

        if (noteId >-1){
            etName.setText(getIntent().getStringExtra(EXTRA_NOTE_NAME));
            etContent.setText(getIntent().getStringExtra(EXTRA_NOTE_Content));

            Cursor c=dbRead.query(NotesDB.TABLE_NAME_MEDIA,null,NotesDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID+"=?",new String[]{noteId +""},null,null,null);
            while (c.moveToNext()){
                adapter.add(new MediaListCellData(c.getString(c.getColumnIndex(NotesDB.COLUMN_NAME_MEDIA_PATH)),c.getInt(c.getColumnIndex(NotesDB.COLUMN_NAME_ID))));
            }
            adapter.notifyDataSetChanged();
        }


        findViewById(R.id.btnSave).setOnClickListener(btnClickHandler);
        findViewById(R.id.btnCancel).setOnClickListener(btnClickHandler);
        findViewById(R.id.btnAddPhoto).setOnClickListener(btnClickHandler);
        findViewById(R.id.btnAddVideo).setOnClickListener(btnClickHandler);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

       switch (requestCode){
           case REQUEST_CODE_GET_PHOTO:
           case REQUEST_CODE_GET_VIDEO:
               if (requestCode==RESULT_OK){
                   adapter.add(new MediaListCellData(currentPath));
                   adapter.notifyDataSetChanged();
               }

               break;

           default:
               break;
       }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public File getMediaDir(){
        File dir=new File(Environment.getExternalStorageDirectory(),"NotesMedia");
        if (!dir.exists()){
            dir.mkdirs();
        }
        return dir;
    }


    public void saveMedia(int noteId){

        MediaListCellData data;
        ContentValues cv;
        for (int i=0;i<adapter.getCount();i++){
            data=adapter.getItem(i);
            if (data.id<=-1){
                cv=new ContentValues();
                cv.put(NotesDB.COLUMN_NAME_MEDIA_PATH,data.path);
                cv.put(NotesDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID,noteId);
                dbWrite.insert(NotesDB.TABLE_NAME_MEDIA,null,cv);
            }
        }
    }

    public int saveNote(){

        ContentValues cv =new ContentValues();
        cv.put(NotesDB.COLUMN_NAME_NOTE_NAME,etName.getText().toString());
        cv.put(NotesDB.COLUMN_NAME_NOTE_CONTENT,etContent.getText().toString());

        cv.put(NotesDB.COLUMN_NAME_NOTE_DATE,new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));

        if ((noteId>-1)){

            dbWrite.update(NotesDB.TABLE_NAME_NOTES,cv,NotesDB.COLUMN_NAME_ID+"=?",new String[]{noteId+""});

            return noteId;
        }else{
            return (int) dbWrite.insert(NotesDB.TABLE_NAME_NOTES,null,cv);
        }


    }
    @Override
    protected void onDestroy() {
        dbRead.close();
        dbWrite.close();

        super.onDestroy();
    }

    //呈现media列表
    static  class MediaAdapter extends BaseAdapter{

        public MediaAdapter(Context context){
            this.context=context;
        }

        public void add(MediaListCellData data){
            list.add(data);

        }


        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public MediaListCellData getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null){
                convertView= LayoutInflater.from(context).inflate(R.layout.media_list_cell,null);
            }
            MediaListCellData data=getItem(position);

            ImageView ivIcon= (ImageView) convertView.findViewById(R.id.ivIcon);
            TextView  tvPath= (TextView) convertView.findViewById(R.id.tvPath);

            ivIcon.setImageResource(data.iconId);
            tvPath.setText(data.path);
            return convertView;
        }

        private Context context;
        private List<MediaListCellData>list= new ArrayList<MediaListCellData>();

    }


    static class MediaListCellData{


        public MediaListCellData(String path){
            this.path=path;

            if (path.endsWith(".jpg")){
                iconId=R.drawable.archer;
            }else if (path.endsWith("mp4")){
                iconId=R.drawable.sky;
            }

        }

        public MediaListCellData(String path,int id){
            this(path);
            this.id=id;
        }
        int id =-1;
        String path="";
        int iconId=R.mipmap.ic_launcher;
    }

    private int noteId =-1;
    private EditText etName,etContent;
    private MediaAdapter adapter;
    private SQLiteDatabase dbRead,dbWrite;
    private NotesDB db;
    private String currentPath=null;
    public static final int REQUEST_CODE_GET_PHOTO=1;
    public static final int REQUEST_CODE_GET_VIDEO=2;



    public static final String EXTRA_NOTE_ID="noteId";
    public static final String EXTRA_NOTE_NAME="noteName";
    public static final String EXTRA_NOTE_Content="noteContent";

}
