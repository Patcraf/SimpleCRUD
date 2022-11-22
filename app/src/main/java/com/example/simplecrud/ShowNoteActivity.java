package com.example.simplecrud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShowNoteActivity extends AppCompatActivity {

    private static final String TAG = "ShowNoteActivity";
    private RecyclerView rvShowAllNotes;
    private FirebaseFirestore db;

    private NoteAdapter adapter;
    private List<NoteModel> notesList;

    private void initUI(){
        rvShowAllNotes = findViewById(R.id.rvShowAllNotes);
        rvShowAllNotes.hasFixedSize();
        rvShowAllNotes.setLayoutManager(new LinearLayoutManager(this));

    }

    private void  initFirestore(){
        db = FirebaseFirestore.getInstance();
    }

    private void  initAdapter(){
             notesList = new ArrayList<>();
             adapter = new NoteAdapter(this, notesList);
             rvShowAllNotes.setAdapter(adapter);
    }

    public void readDataFromFirestore() {
        db.collection("Notes").orderBy("id").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       notesList.clear();
                       for (DocumentSnapshot documentSnapshot: task.getResult()){
                           NoteModel noteModel =new NoteModel(

                                   documentSnapshot.getString("id"),
                                   documentSnapshot.getString("title"),
                                   documentSnapshot.getString("content")
                           );
                           notesList.add(noteModel);
                           Log.i(TAG, "onComplete: " + documentSnapshot.getId());
                       }
                       adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShowNoteActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notes);

        initUI();
        initFirestore();
        initAdapter();
        readDataFromFirestore();
    }
}