package com.example.sgc109.webtoonlive;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateLiveActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private EditText mTitleEditText;

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, CreateLiveActivity.class);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_live);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mTitleEditText = findViewById(R.id.create_live_edit_text);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_start_live:
                if(mTitleEditText.getText().toString().equals("")){
                    Toast.makeText(this, "제목을 입력하세요!", Toast.LENGTH_SHORT).show();
                    return true;
                }
                DatabaseReference ref = mDatabase.child(getString(R.string.firebase_db_live_list));
                String key = ref.push().getKey();
//                if (mExistOnAirLive) {
//                    Toast.makeText(this, "There is already On-Air Live show", Toast.LENGTH_SHORT).show();
//                    return true;
//                }
//                mExistOnAirLive = true;
                LiveInfo liveInfo = new LiveInfo(key, mTitleEditText.getText().toString(), getString(R.string.live_state_on_air));
                Intent intent = WriterLiveActivity.newIntent(this, liveInfo.key);
                startActivity(intent);
                ref
                        .child(key)
                        .setValue(liveInfo);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_create_live, menu);
        return true;
    }
}
