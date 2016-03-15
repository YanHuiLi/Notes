package android.archer.com.notes;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;

/**
 * @function 图片的查看器
 * Created by Archer on 2016/3/15.
 */
public class AtyPhotoViewer extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iv=new ImageView(this);
        setContentView(iv);

        String path =getIntent().getStringExtra(EXTRA_PATH);
        if (path!=null){
            iv.setImageURI(Uri.fromFile(new File(path)));
        }else{
            finish();
        }
    }

    private ImageView iv;


    public  static final String  EXTRA_PATH="path";
}
