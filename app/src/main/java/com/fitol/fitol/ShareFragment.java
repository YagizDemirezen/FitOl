package com.fitol.fitol;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.widget.Toast;

import com.fitol.FitOl.R;
import com.fitol.FitOl.databinding.FragmentShareBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class ShareFragment extends Fragment {

    private FragmentShareBinding binding;
    Uri image;
    Bitmap cameraImage;
    String[] alertDialogItems = {"📂 Cihazdan Seç", "📸 Fotoğraf Çek"};
    String message, UserID, username;


    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageImage;
    String downloadUrl;


    public ShareFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShareBinding.inflate(inflater, container, false);
        binding.shareProggressBar.setVisibility(View.INVISIBLE);
        binding.proggressText.setVisibility(View.INVISIBLE);


        if (auth.getCurrentUser() != null) {
            UserID = auth.getCurrentUser().getUid();
            database.collection("USERS").document(UserID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists())
                    {
                        username = documentSnapshot.getString("username");
                    }
                }
            });
        }


        binding.shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
                alertDialogBuilder.setIcon(R.drawable.alerticon);
                alertDialogBuilder.setTitle("Fotoğrafı nereden seçmek istersin?");
                alertDialogBuilder.setItems(alertDialogItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            // Galeri seçilirse
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1001); //requestCode 1001 = galeri izni almam için gerekli kod
                                } else {
                                    galleryOpener();
                                }
                            } else {
                                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
                                } else {
                                    galleryOpener();
                                }
                            }
                        } else if (i == 1) {
                            // Kamera seçildi
                            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                cameraOpener();
                            } else {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1011); //requestCode 1011 = kamera açma izni almam için gerekli kod
                                cameraOpener();
                            }
                        }

                    }
                });
                alertDialogBuilder.show();
            }
        });


        binding.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.shareProggressBar.setProgress(0);
                binding.proggressText.setText("");

                if (!binding.shareText.getText().toString().isEmpty()) {
                    message = binding.shareText.getText().toString();
                }
                else
                {
                    message = "";
                }

                storageImage = storage.getReference().child("IMAGES/" + "IMG_" + UserID + System.currentTimeMillis() + ".jpg");




                if (image != null)
                {
                    binding.shareProggressBar.setVisibility(View.VISIBLE);
                    binding.proggressText.setVisibility(View.VISIBLE);
                    storageImage.putFile(image).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progressBarInfo = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            int currentProgress = binding.shareProggressBar.getProgress();
                            int FutureProgress = (int)progressBarInfo;
                            binding.shareProggressBar.setProgress((int) progressBarInfo);

                            ObjectAnimator animator = ObjectAnimator.ofInt(binding.shareProggressBar, "progress", currentProgress, FutureProgress);
                            animator.setDuration(300);
                            animator.start();

                            binding.proggressText.setText("Fotoğraf yükleniyor. İlerleme: %" + (int) FutureProgress);

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                                downloadUrl = uri.toString();
                                addMessage(downloadUrl);
                                Toast.makeText(requireContext(), "Gönderi Paylaşıldı", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireContext(), "Yükleme işlemi başarısız. Yeniden Deneyiniz", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            binding.shareProggressBar.setVisibility(View.INVISIBLE);
                            binding.proggressText.setVisibility(View.INVISIBLE);
                            binding.shareImageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.addphoto));
                            binding.shareText.setText("");
                        }
                    });



                }
                else if (cameraImage != null)
                {

                    ByteArrayOutputStream byte_ = new ByteArrayOutputStream();
                    cameraImage.compress(Bitmap.CompressFormat.JPEG, 100, byte_); // Görüntüyü JPEG formatında sıkıştırıyoruz
                    byte[] cameraImageWithByte = byte_.toByteArray();


                    storageImage.putBytes(cameraImageWithByte).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                            double progressBarInfo = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            int currentProgress = binding.shareProggressBar.getProgress();
                            int FutureProgress = (int)progressBarInfo;
                            binding.shareProggressBar.setProgress((int) progressBarInfo);

                            ObjectAnimator animator = ObjectAnimator.ofInt(binding.shareProggressBar, "progress", currentProgress, FutureProgress);
                            animator.setDuration(300);
                            animator.start();

                            binding.proggressText.setText("Fotoğraf yükleniyor. İlerleme: %" + (int) FutureProgress);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                                downloadUrl = uri.toString();
                                addMessage(downloadUrl);
                                Toast.makeText(requireContext(), "Gönderi Paylaşıldı", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireContext(), "Yükleme işlemi başarısız. Yeniden Deneyiniz", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            binding.shareProggressBar.setVisibility(View.INVISIBLE);
                            binding.proggressText.setVisibility(View.INVISIBLE);
                            binding.shareImageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.addphoto));
                            binding.shareText.setText("");


                        }
                    });


                }

                else
                {
                    Toast.makeText(requireContext(), "Öncelikle bir fotoğraf seçmelisiniz!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return binding.getRoot();
    }

    public void addMessage(String imageURL)
    {
        String postID = UUID.randomUUID().toString();


        database.collection("POSTS").document(postID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    //eğer kullanıcı oradaysa güncelleyecem
                    database.collection("POSTS").document(postID).update("username",username,"imageUrl",storageImage,"message", message);
                }
                else
                {
                    // kullanıcı yoksa yeni veri ekleyecem
                    Map<String, Object> sendMessage = new HashMap<>();
                    sendMessage.put("username",username);
                    sendMessage.put("imageUrl",imageURL);
                    sendMessage.put("message", message);

                    database.collection("POSTS").document(postID).set(sendMessage);
                }
            }
        });
    }


    public void galleryOpener() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryResult.launch(intent);
    }

    public void cameraOpener() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraResult.launch(intent);
    }

    public void setImage(Uri image2) {
        image = image2;
        binding.shareImageView.setImageURI(image2);
        binding.shareMiniText.setText("Fotoğraf başarıyla seçildi ✔");
        binding.shareMiniText.setTextColor(Color.parseColor("#4CAF50"));
        cameraImage = null;
    }

    public void setCameraImage(Bitmap cameraImage2) {
        cameraImage = cameraImage2;
        binding.shareImageView.setImageBitmap(cameraImage2);
        binding.shareMiniText.setText("Fotoğraf başarıyla seçildi ✔");
        binding.shareMiniText.setTextColor(Color.parseColor("#4CAF50"));
        image = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            galleryOpener();
        } else if (requestCode == 1012 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            cameraOpener();
        }

    }

    ActivityResultLauncher<Intent> cameraResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Bundle extras = result.getData().getExtras();
                cameraImage = (Bitmap) extras.get("data");
                setCameraImage(cameraImage);
            }
        }
    });
    ActivityResultLauncher<Intent> galleryResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                image = result.getData().getData();
                setImage(image);
            }
        }
    });
}