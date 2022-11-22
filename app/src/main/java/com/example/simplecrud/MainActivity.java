package com.example.simplecrud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    //var
    private EditText etNoteTitle, etNoteContent;
    private Button btnSaveNote, btnShowNote;
    private TextView tvNoteState;

    //initialiser la firebase 1
    private FirebaseFirestore db;

    //liaison avec le design (id) et var
    private void initUI () {
        etNoteTitle = findViewById(R.id.etNoteTitle);
        etNoteContent = findViewById(R.id.etNoteContent);
        btnSaveNote = findViewById(R.id.btnSaveNote);
        btnShowNote = findViewById(R.id.btnShowAllNotes);
        tvNoteState = findViewById(R.id.tvNoteState);
    }

    //initialiser la firebase 2
    private void  initFirebaseTools(){
        db = FirebaseFirestore.getInstance();
    }

    //Action du Click
    private void clicSaveNote() {
        btnSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // String id = UUID.randomUUID().toString();
                String title = etNoteTitle.getText().toString();
                String content = etNoteContent.getText().toString();
                Bundle bundle = getIntent().getExtras();
                if (bundle != null){
                    String id = bundle.getString("uId");
                    updateDocumentInFirestore(id, title, content);
                } else {
                    //Recuperation des donnees du formulaire
                    String id = UUID.randomUUID().toString();

                    // Appel de la methode pour la creation en base
                    createDocumentInFirestore(id, title, content);
                }
            }
        });
    }

    //Action du Click
    private void clicShowAllNotes() {
        btnShowNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Save all notes", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, ShowNoteActivity.class));
            }
        });
    }

    /**CREATE**/
    // creer le document dans Firestore
    private void createDocumentInFirestore(String id, String title, String content){
        if (!title.isEmpty() && !content.isEmpty()){
            // Creation d 1 tableau qui cotoient les data a envoyer sur Firestore
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("title", title);
            map.put("content", content);

            db.collection("Notes").document(id).set(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this, "Note saved !", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Failed" +e, Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show();
        }

    }

    /**UPDATE**/
    private void updateDocumentInFirestore(String id, String title, String content) {

        db.collection("Notes").document(id).update("title", title, "content", content)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Data updated", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(MainActivity.this, "Error" + task, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void showDataToUpdateFromTheBundle(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            //Changement du text des widgets
            btnSaveNote.setText("Update note to Firestore");
            tvNoteState.setText("Update note");
            btnShowNote.setVisibility(View.GONE);

            //Recuperation des donnees via bundle
            String uId = bundle.getString("uId");
            String uTitle = bundle.getString("uTitle");
            String uContent = bundle.getString("uContent");

            //Asssociation des donnees au formulaire
            etNoteTitle.setText(uTitle);
            etNoteContent.setText(uContent);
        } else {
            btnSaveNote.setText("Save note to Firestore");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //declaration des methode -- !! important -> par ordre
        initUI();
        initFirebaseTools();
        showDataToUpdateFromTheBundle();
        clicSaveNote();
        clicShowAllNotes();

        }
}