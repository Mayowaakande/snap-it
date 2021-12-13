package com.app.snapsapp.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.snapsapp.R;
import com.app.snapsapp.activities.MainActivity;
import com.app.snapsapp.activities.SnapDetailsActivity;
import com.app.snapsapp.models.SnapModelClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BookmarksFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    View view;
    Context context;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    public static List<SnapModelClass> list;
    TextView textView;
    public static SnapModelClass snap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        context = container.getContext();

//        showProgressDialog();

        MainActivity.btnCreateSnap.setVisibility(View.GONE);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Bookmarks").child(userId);
        list = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); ;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,1);
        recyclerView.setLayoutManager(gridLayoutManager);
        textView = view.findViewById(R.id.textView);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(MainActivity.bookmarkList.size()>0){
            recyclerView.setAdapter(null);
            SnapsListAdapter adapter = new SnapsListAdapter(context, MainActivity.bookmarkList);
            recyclerView.setAdapter(adapter);
        }else {
            textView.setText("No Bookmarks!");
        }

//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                list.clear();
//                textView.setText("");
//                recyclerView.setAdapter(null);
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    SnapModelClass invoice = snapshot.getValue(SnapModelClass.class);
//                    list.add(invoice);
//                }
//                if(list.size()>0){
//                    SnapsListAdapter adapter = new SnapsListAdapter(context, list);
//                    recyclerView.setAdapter(adapter);
//                }else {
//                    textView.setText("No Bookmarks!");
//                }
//                hideProgressDialog();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {hideProgressDialog(); }});


    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Loading data..");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public static class SnapsListAdapter extends RecyclerView.Adapter<SnapsListAdapter.ImageViewHolder>{
        private Context mcontext ;
        private List<SnapModelClass> muploadList;

        public SnapsListAdapter(Context context , List<SnapModelClass> uploadList ) {
            mcontext = context ;
            muploadList = uploadList ;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mcontext).inflate(R.layout.snaps_list_layout, parent , false);
            return (new ImageViewHolder(v));
        }

        @Override
        public void onBindViewHolder(final ImageViewHolder holder, final int position) {

            final SnapModelClass model = muploadList.get(position);
            holder.tvDescription.setText(model.getDescription());
            Picasso.with(mcontext).load(model.getImageUrl()).placeholder(R.drawable.ic_logo).into(holder.imgSnap);

            holder.imgLocation.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.bookmark = model;
                    Intent intent = new Intent(mcontext, SnapDetailsActivity.class);
                    intent.putExtra("key","bookmark");
                    mcontext.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return muploadList.size();
        }

        public static  class ImageViewHolder extends RecyclerView.ViewHolder{
            public ImageView imgSnap;
            public ImageView imgLocation;
            public TextView tvDescription;

            public ImageViewHolder(View itemView) {
                super(itemView);

                imgSnap = itemView.findViewById(R.id.imgSnap);
                imgLocation = itemView.findViewById(R.id.imgLocation);
                tvDescription = itemView.findViewById(R.id.tvDescription);
            }
        }
    }
}
