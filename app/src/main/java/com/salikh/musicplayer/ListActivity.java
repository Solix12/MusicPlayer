package com.salikh.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import soup.neumorphism.NeumorphCardView;

public class ListActivity extends AppCompatActivity {

    private ListView listView;
    private String[] item;
    private NeumorphCardView back_list_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.white));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));

        loadViews();
        back_list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        runTimePermission();

    }

    private void loadViews() {
        listView = findViewById(R.id.list_item);
        back_list_btn = findViewById(R.id.back_list_btn);
    }

    public void runTimePermission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        displaySong();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> findSong(File file) {
        ArrayList<File> arrayList = new ArrayList<>();

        File[] fileq = file.listFiles();
        if (fileq != null) {
            for (File singlefile : fileq) {
                if (singlefile.isDirectory() && !singlefile.isHidden()) {
                    arrayList.addAll(findSong(singlefile));
                } else {
                    if (singlefile.getName().endsWith(".mp3") || singlefile.getName().endsWith(".wav")) {
                        arrayList.add(singlefile);
                    }
                }

            }
        }
        return arrayList;
    }

    public void displaySong() {
        final ArrayList<File> mySong = findSong(Environment.getExternalStorageDirectory());

        item = new String[mySong.size()];
        for (int i = 0; i < mySong.size(); i++) {
            item[i] = mySong.get(i).getName().replace(".mp3", "").replace(".wav", "");
        }
       /* ArrayAdapter<String> myAdapter = new ArrayAdapter<String >(this,R.layout.activity_list , item);
        listView.setAdapter(myAdapter);*/

        CostumeAdapter costumeAdapter = new CostumeAdapter();
        listView.setAdapter(costumeAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songName = (String) listView.getItemAtPosition(i);
                startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                        .putExtra("songs", mySong)
                        .putExtra("songName", songName)
                        .putExtra("pos", i));
                Animatoo.animateZoom(ListActivity.this);
            }
        });
    }


    class CostumeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return item.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myView = getLayoutInflater().inflate(R.layout.item_music, null);

            TextView textSong = myView.findViewById(R.id.textName);
            textSong.setSelected(true);
            textSong.setText(item[i]);

            return myView;
        }
    }

}