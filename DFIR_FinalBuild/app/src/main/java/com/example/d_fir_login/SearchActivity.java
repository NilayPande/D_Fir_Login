package com.example.d_fir_login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d_fir_login.Model.Case;
import com.example.d_fir_login.RecycleView.AdapterClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ArrayList<Case> list;
    SearchView searchView;
    RecyclerView recyclerView;
    FirebaseDatabase database;
    DatabaseReference ref;
    Button btnCreateNewCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Cases");

        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);

        btnCreateNewCase = findViewById(R.id.btnCreateNewCase);

        btnCreateNewCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, NewCaseActivity.class);
                startActivity(intent);
            }
        });

        searchView.setVisibility(View.GONE);

    }

    public void showCase(View view){
        Intent intent = new Intent(SearchActivity.this, DisplayCaseActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(ref != null){
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        list = new ArrayList<>();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            list.add(dataSnapshot.getValue(Case.class));
                        }
                        AdapterClass adapterClass = new AdapterClass(list);
                        recyclerView.setAdapter(adapterClass);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SearchActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
        if(searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    search(s);
                    return false;
                }
            });
        }
        else
            Toast.makeText(this, "Nahi Chala", Toast.LENGTH_SHORT).show();
    }

    private void search(String s) {
        ArrayList<Case> arrayList = new ArrayList<>();
        for(Case obj : arrayList){
            Toast.makeText(this, obj.getOfficerName() + " Hi", Toast.LENGTH_SHORT).show();
            if(obj.getOfficerName().toLowerCase().contains(s.toLowerCase())){
                arrayList.add(obj);
            }
        }
        AdapterClass adapterClass = new AdapterClass(arrayList);
        recyclerView.setAdapter(adapterClass);
    }

}