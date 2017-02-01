package com.example.abu.registration;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.Precision.Component.FP.BiomSDK.BiometricComponent;
import com.Precision.Component.FP.BiomSDK.ErrorCode;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button scan;
    EditText USN;
    Spinner collegeSpinner;
    Spinner branchSpinner;
    Spinner yearSpinner;
    Spinner sectionSpinner;
    ArrayAdapter<CharSequence> collegeAdapter;
    ArrayAdapter<CharSequence> branchAdapter;
    ArrayAdapter<CharSequence> yearAdapter;
    ArrayAdapter<CharSequence> sectionAdapter;
    List<byte[]> isoTemplates;
    byte[] isoFPTemplate;
    int imageWidth;
    int imageHeight;
    byte[] rawImage;
    byte[] isoTemplate;
    byte[] isoImage;
    String FilePath="";
    String initialString;
    String userInput;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText USN = (EditText) findViewById(R.id.USN);
        scan = (Button) findViewById(R.id.scan);
        collegeSpinner = (Spinner) findViewById(R.id.college);
        branchSpinner = (Spinner) findViewById(R.id.branch);
        yearSpinner = (Spinner) findViewById(R.id.year);
        sectionSpinner = (Spinner) findViewById(R.id.section);




        collegeAdapter = ArrayAdapter.createFromResource(this,R.array.colleges,android.R.layout.simple_spinner_item);
        collegeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        collegeSpinner.setAdapter(collegeAdapter);

        branchAdapter = ArrayAdapter.createFromResource(this,R.array.branch,android.R.layout.simple_spinner_item);
        collegeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branchSpinner.setAdapter(branchAdapter);

        yearAdapter = ArrayAdapter.createFromResource(this,R.array.year,android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        sectionAdapter = ArrayAdapter.createFromResource(this,R.array.section,android.R.layout.simple_spinner_item);
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sectionSpinner.setAdapter(sectionAdapter);
        /*college.setOnItemSelectedListener(new AdapterView.onItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position,long id)
            {
                Toast.makeText(getBaseContext(),parent.getItemAtPosition(position)+"selected", Toast.LENGTH_LONG).show();
            }
        });
*/
        collegeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initialString="Select College";
                if((parent.getItemAtPosition(position)).equals(initialString))
                {

                }
                else
                {
                    Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " selected", Toast.LENGTH_LONG).show();
                    FilePath = FilePath +"/"+ parent.getItemAtPosition(position);
                    Toast.makeText(getApplicationContext(), FilePath, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initialString="Select branch";
                if((parent.getItemAtPosition(position)).equals(initialString))
                {

                }
                else
                {
                    Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " selected", Toast.LENGTH_LONG).show();
                    FilePath = FilePath +"/"+ parent.getItemAtPosition(position);
                    Toast.makeText(getApplicationContext(), FilePath, Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initialString="Select section";
                if((parent.getItemAtPosition(position)).equals(initialString))
                {

                }
                else
                {
                    Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " selected", Toast.LENGTH_LONG).show();
                    FilePath = FilePath +"/"+ parent.getItemAtPosition(position);
                    Toast.makeText(getApplicationContext(), FilePath, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initialString="Select year";
                if((parent.getItemAtPosition(position)).equals(initialString))
                {

                }
                else
                {
                    Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " selected", Toast.LENGTH_LONG).show();
                    FilePath = FilePath +"/"+ parent.getItemAtPosition(position);
                    Toast.makeText(getApplicationContext(), FilePath, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        USN.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
            {
                boolean handled = false;
                if(i== EditorInfo.IME_ACTION_DONE){
                    userInput=textView.getText().toString();
                    handled=true;
                }
                FilePath=FilePath+"/"+userInput;
                Toast.makeText(getApplicationContext(), FilePath, Toast.LENGTH_SHORT).show();
                return handled;
            }
        });




        Toast.makeText(getApplicationContext(), FilePath, Toast.LENGTH_SHORT).show();


        //emdof spinner

        final BiometricComponent biometricComponent = new BiometricComponent(MainActivity.this);
        isoTemplates = new ArrayList<byte[]>();
       /* try {
            File f = new File("/sdcard/ISO/");
            for (String file : f.list()) {
                FileInputStream fin = new FileInputStream("/sdcard/ISO/" +file);
                isoFPTemplate = new byte[fin.available()];
                fin.read(isoFPTemplate);
                fin.close();
                if (isoFPTemplate != null) {
                    isoTemplates.add(isoFPTemplate);
                }
            }
        } catch (Exception e) {

        } finally {
        }
*/
        //for options
        /*college.setOnItemSelectedListener(this);
        List<String> college = new ArrayList<String>();
        college.add("PESIT");

        branch.setOnItemSelectedListener(this);
        List<String> branch = new ArrayList<String>();
        branch.add("CSE");
        branch.add("ISE");
        branch.add("Mech");
        branch.add("EC");

        year.setOnItemSelectedListener(this);
        List<String> year = new ArrayList<String>();
        year.add("2014");
        year.add("2015");
        year.add("2016");
        year.add("2017");

        section.setOnItemSelectedListener(this);
        List<String> section = new ArrayList<String>();
        section.add("A");
        section.add("B");
        section.add("C");*/

        //end of options

        //capturing the image
        scan.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("SdCardPath")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //fpImageView.setImageBitmap(null);

                int iResult = biometricComponent.FPCapture(1);
                if (iResult == ErrorCode.LS_OK) {
                    imageHeight = biometricComponent.getImageHeight();
                    imageWidth = biometricComponent.getImageWidth();
                    rawImage = biometricComponent.getRawImageData();
                    isoTemplate = biometricComponent.getISOTemplate();
                    isoImage = biometricComponent.getISOImage();
                    //fpImageView.setImageBitmap(biometricComponent.RawToBitmap(rawImage, imageWidth, imageHeight));
                    try {
                        FileOutputStream fout = new FileOutputStream("/sdcard/" +FilePath+"/LiveFMRTemplate");
                        fout.write(isoTemplate);
                        fout.close();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Exception in writing file", Toast.LENGTH_SHORT).show();
                    } finally {

                    }

                    try {
                        FileOutputStream fout = new FileOutputStream("/sdcard/" +FilePath+"/FIRImage");
                        fout.write(isoImage);
                        fout.close();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Exception in writing file", Toast.LENGTH_SHORT).show();
                    } finally {

                    }
                    if (isoImage != null || isoTemplate != null)
                        Toast.makeText(getApplicationContext(), "Image Capture Success", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), "Image Capture Failed", Toast.LENGTH_SHORT).show();
                } else if (iResult == ErrorCode.LS_FIR_FAILED) {
                    Toast.makeText(MainActivity.this, "Generate ISO Image Failed", Toast.LENGTH_SHORT).show();
                } else if (iResult == ErrorCode.LS_IMAGE_NOT_CAPTURED) {
                    Toast.makeText(MainActivity.this, "Image Captured Failed", Toast.LENGTH_SHORT).show();
                } else if (iResult == ErrorCode.LS_LICENSE_FAILED) {
                    Toast.makeText(MainActivity.this, "Image capture License failed", Toast.LENGTH_SHORT).show();
                } else if (iResult == ErrorCode.LS_NO_SCANNER_FOUND) {
                    Toast.makeText(MainActivity.this, "Please connect the scanner", Toast.LENGTH_SHORT).show();
                } else if (iResult == ErrorCode.LS_SCANNER_ALREADY_INITIALIZED) {
                    Toast.makeText(MainActivity.this, "Scanner already initialized", Toast.LENGTH_SHORT).show();
                } else if (iResult == ErrorCode.LS_LICENSE_NOT_FOUND) {
                    Toast.makeText(MainActivity.this, "License Not Found", Toast.LENGTH_SHORT).show();
                } else if (iResult == ErrorCode.LS_EXCEPTION) {
                    Toast.makeText(MainActivity.this, "Exception in process", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Scanner initialization failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //end of capture
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    /*college.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position,long id)
            {
                Toast.makeText(getBaseContext(),parent.getItemAtPosition(position)+"selected", Toast.LENGTH_LONG).show();
            }
        });
*/


}
