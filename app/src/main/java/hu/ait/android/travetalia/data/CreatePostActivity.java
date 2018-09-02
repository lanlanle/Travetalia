package hu.ait.android.travetalia.data;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hu.ait.android.travetalia.R;
import hu.ait.android.travetalia.ScratchmapActivity;

public class CreatePostActivity extends AppCompatActivity {

    @BindView(R.id.etLocation)
    AutoCompleteTextView etLocation;
    @BindView(R.id.etCaption)
    EditText etCaption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        ButterKnife.bind(this);


        String[] places = getResources().getStringArray(R.array.places);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,places);
        etLocation.setAdapter(adapter);


    }
    @OnClick(R.id.btnPost)
    void sendClick() {
        if (imgAttach.getVisibility() == View.GONE) {
            uploadPost();
        } else {
            try {
                uploadPostWithImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @OnClick(R.id.btnAttach)
    void attachClick() {
//        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intentCamera, 101);

        Intent intentCamera = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intentCamera, 101);
    }

    @BindView(R.id.imgAttach)
    ImageView imgAttach;

    private void uploadPost(String... imageUrl) {




        String key = FirebaseDatabase.getInstance().getReference().child("posts").push().getKey();
        final Post newPost = new Post(
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                etLocation.getText().toString(),
                etCaption.getText().toString());

        newPost.setLat(getIntent().getDoubleExtra(ScratchmapActivity.KEY_LAT, 0));
        newPost.setLng(getIntent().getDoubleExtra(ScratchmapActivity.KEY_LON, 0));
        newPost.setCountry(getIntent().getStringExtra(ScratchmapActivity.KEY_COUNTRY));
        newPost.setzIndex(getIntent().getIntExtra(ScratchmapActivity.KEY_COUNTRY,0));


        if (imageUrl != null && imageUrl.length>0) {
            newPost.setImageUrl(imageUrl[0]);
        }

        FirebaseDatabase.getInstance().getReference().child("posts").child(key).setValue(newPost).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(CreatePostActivity.this, "Post created", Toast.LENGTH_SHORT).show();

                Intent intenResult = new Intent();
                intenResult.putExtra(ScratchmapActivity.KEY_POST, newPost);
                setResult(RESULT_OK, intenResult);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101 && resultCode == RESULT_OK) {

            Uri targetUri = data.getData();
            final Bitmap imageBitmap;
            try {
                imageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                imgAttach.setImageBitmap(imageBitmap);
                imgAttach.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }
    }

    public void uploadPostWithImage() throws Exception {
        imgAttach.setDrawingCacheEnabled(true);
        imgAttach.buildDrawingCache();
        Bitmap bitmap = imgAttach.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageInBytes = baos.toByteArray();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String newImage = URLEncoder.encode(UUID.randomUUID().toString(), "UTF-8")+".jpg";
        StorageReference newImageRef = storageRef.child(newImage);
        StorageReference newImageImagesRef = storageRef.child("images/"+newImage);
        newImageRef.getName().equals(newImageImagesRef.getName());    // true
        newImageRef.getPath().equals(newImageImagesRef.getPath());    // false

        UploadTask uploadTask = newImageImagesRef.putBytes(imageInBytes);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(CreatePostActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                uploadPost(taskSnapshot.getDownloadUrl().toString());
            }
        });
    }
}
