package com.example.practicaocr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.FaceAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.TextAnnotation;

import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
public Vision vision;
public ImageView imagen;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imagen = (ImageView)findViewById(R.id.imageView2);


        Vision.Builder visionBuilder = new Vision.Builder(new NetHttpTransport(),
                new AndroidJsonFactory(), null);
        visionBuilder.setVisionRequestInitializer(new VisionRequestInitializer("apikey"));
                 vision = visionBuilder.build();

    }
    public void botonClickCargar(View view){
        openGallery();
    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            imagen.setImageURI(imageUri);
        }
    }

    public void botonClick(View view){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                imagen=(ImageView) findViewById(R.id.imageView2);
                BitmapDrawable drawable = (BitmapDrawable) imagen.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                bitmap = scaleBitmapDown(bitmap,1200 );
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                byte[] imageInByte = stream.toByteArray();
                    //paso 1
                Image inputImage = new Image();
                inputImage.encodeContent(imageInByte);

                //paso 2 Feature

                Feature desiredFeature = new Feature();
                desiredFeature.setType("TEXT_DETECTION");


                // paso 3 arma la sulicitud(es)
                AnnotateImageRequest request = new AnnotateImageRequest();
                request.setImage(inputImage);
                request.setFeatures(Arrays.asList(desiredFeature));
                BatchAnnotateImagesRequest batchRequest = new
                        BatchAnnotateImagesRequest();
                batchRequest.setRequests(Arrays.asList(request));
                //paso 4 asignamos al control visionbuilder la solicitud

                try {
                Vision.Images.Annotate  annotateRequest =
                            vision.images().annotate(batchRequest);
                //paso 5 enviamos la solicitud
                    annotateRequest.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse batchResponse =
                            annotateRequest.execute();

                    //paso 6 obtener la respuesta
                    TextAnnotation text = batchResponse.getResponses().get(0).getFullTextAnnotation();

                    final  String result =text.getText();

                    //paso 7 asignar la UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView imageDetail = (TextView)findViewById(R.id.textView2);
                            imageDetail.setText(result);
                        }
                    });
                    //return text.getText();

                } catch (IOException e) {
                    e.printStackTrace();
                }




            }
        });
    }
    public void botonClickobjetos(View view){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                imagen=(ImageView) findViewById(R.id.imageView2);
                BitmapDrawable drawable = (BitmapDrawable) imagen.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                bitmap = scaleBitmapDown(bitmap,1200 );
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                byte[] imageInByte = stream.toByteArray();
                //paso 1
                Image inputImage = new Image();
                inputImage.encodeContent(imageInByte);

                //paso 2 Feature

                Feature desiredFeature = new Feature();
                desiredFeature.setType("LABEL_DETECTION");


                // paso 3 arma la sulicitud(es)
                AnnotateImageRequest request = new AnnotateImageRequest();
                request.setImage(inputImage);
                request.setFeatures(Arrays.asList(desiredFeature));
                BatchAnnotateImagesRequest batchRequest = new
                        BatchAnnotateImagesRequest();
                batchRequest.setRequests(Arrays.asList(request));
                //paso 4 asignamos al control visionbuilder la solicitud

                try {
                    Vision.Images.Annotate  annotateRequest =
                            vision.images().annotate(batchRequest);
                    //paso 5 enviamos la solicitud
                    annotateRequest.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse batchResponse =
                            annotateRequest.execute();

                    //paso 6 obtener la respuesta
                     final StringBuilder message = new StringBuilder("I found these things:\n\n");
                    List<EntityAnnotation> labels =
                            batchResponse.getResponses().get(0).getLabelAnnotations();
                    if (labels != null) {
                        for (EntityAnnotation label : labels) {
                            message.append(String.format(Locale.US, "%.3f: %s",
                                    label.getScore(), label.getDescription()));
                            message.append("\n");
                        }
                    } else {
                        message.append("nothing");
                    }


                    //paso 7 asignar la UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView imageDetail = (TextView)findViewById(R.id.textView2);
                            imageDetail.setText(message);
                        }
                    });
                    //return text.getText();

                } catch (IOException e) {
                    e.printStackTrace();
                }




            }
        });
    }

    public void botonClickFaces(View view){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                imagen=(ImageView) findViewById(R.id.imageView2);
                BitmapDrawable drawable = (BitmapDrawable) imagen.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                bitmap = scaleBitmapDown(bitmap,1200 );
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                byte[] imageInByte = stream.toByteArray();
                //paso 1
                Image inputImage = new Image();
                inputImage.encodeContent(imageInByte);

                //paso 2 Feature

                Feature desiredFeature = new Feature();
                desiredFeature.setType("FACE_DETECTION");


                // paso 3 arma la sulicitud(es)
                AnnotateImageRequest request = new AnnotateImageRequest();
                request.setImage(inputImage);
                request.setFeatures(Arrays.asList(desiredFeature));
                BatchAnnotateImagesRequest batchRequest = new
                        BatchAnnotateImagesRequest();
                batchRequest.setRequests(Arrays.asList(request));
                //paso 4 asignamos al control visionbuilder la solicitud

                try {
                    Vision.Images.Annotate  annotateRequest = vision.images().annotate(batchRequest);
                    //paso 5 enviamos la solicitud
                    annotateRequest.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse batchResponse = annotateRequest.execute();

                    //paso 6 obtener la respuesta
                    List<FaceAnnotation> faces = batchResponse.getResponses()
                            .get(0).getFaceAnnotations();
                    int numberOfFaces = faces.size();
                    String likelihoods =
                            "";
                    for(int i=0; i<numberOfFaces; i++) {
                        likelihoods += "\n It is " + faces.get(i).getJoyLikelihood() +
                                " that face " + i + " is happy";
                    }
                    final String message = "This photo has " + numberOfFaces + " faces" + likelihoods;


                    //paso 7 asignar la UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView imageDetail = (TextView)findViewById(R.id.textView2);
                            imageDetail.setText(message);
                        }
                    });
                    //return text.getText();

                } catch (IOException e) {
                    e.printStackTrace();
                }




            }
        });
    }
    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;
        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }
}
