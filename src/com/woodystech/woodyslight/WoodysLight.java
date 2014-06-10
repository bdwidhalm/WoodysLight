package com.woodystech.woodyslight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import com.google.ads.*;

public class WoodysLight extends Activity
{
    ImageButton btnSwitch;
 
    private Camera camera;
    private boolean isFlashOn = false;
    private boolean hasFlash;
    Parameters params;
    MediaPlayer mp;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // flash switch button
        btnSwitch = (ImageButton) findViewById(R.id.btnSwitch);
        
        hasFlash = getApplicationContext().getPackageManager()
        .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
 
        if (!hasFlash) 
        {
            // device doesn't support flash
            // Show alert message and close the application
            AlertDialog alert = new AlertDialog.Builder(WoodysLight.this).create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton("OK", new DialogInterface.OnClickListener() 
            {
                public void onClick(DialogInterface dialog, int which) 
                {
                    // closing the application
                    finish();
                }
            });
            alert.show();
            return;
        }
        
        getCamera();
        
        toggleButtonImage();
        
        // Look up the AdView and load request
        AdView adView = (AdView)this.findViewById(R.id.adViewMain);
        AdRequest adRequest = new AdRequest();
        adView.loadAd(adRequest);
        
    }
    
    // light switch clicked
    public void lightSwitch(View v)
    {
        if (isFlashOn) 
        {
            // turn off flash
            turnOffFlash();
        } 
        else 
        {
            // turn on flash
            turnOnFlash();
        }
    }
    
    // getting camera parameters
    private void getCamera() 
    {
        if (camera == null) 
        {
            try 
            {
                camera = Camera.open();
                params = camera.getParameters();
            } 
            catch (RuntimeException e) 
            {
                Log.e("Camera Error. Failed to Open. Error: ", e.getMessage());
            }
        }
    }
 
     /*
     * Turning On flash
     */
    private void turnOnFlash() 
    {
        if (!isFlashOn) 
        {
            if (camera == null || params == null) 
            {
                return;
            }
            // play sound
            playSound();
             
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;
             
            // changing button/switch image
            toggleButtonImage();
        }
     
    }
    
    /*
     * Turning Off flash
     */
    private void turnOffFlash() 
    {
        if (isFlashOn) 
        {
            if (camera == null || params == null) 
            {
                return;
            }
            // play sound
            playSound();
             
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;
             
            // changing button/switch image
            toggleButtonImage();
        }
    }
    
    /*
     * Toggle switch button images
     * changing image states to on / off
     * */
    private void toggleButtonImage()
    {
        if(isFlashOn)
        {
            btnSwitch.setImageResource(R.drawable.btn_switch_on);
        }
        else
        {
            btnSwitch.setImageResource(R.drawable.btn_switch_off);
        }
    }
    
    /*
     * Playing sound
     * will play button toggle sound on flash on / off
     * */
    private void playSound()
    {
        if(isFlashOn)
        {
            mp = MediaPlayer.create(WoodysLight.this, R.raw.light_switch_off);
        }
        else
        {
            mp = MediaPlayer.create(WoodysLight.this, R.raw.light_switch_on);
        }
        mp.setOnCompletionListener(new OnCompletionListener() 
        {
            @Override
            public void onCompletion(MediaPlayer mp) 
            {
                mp.release();
            }
        }); 
        mp.start();
    }
    
    @Override
    protected void onDestroy() 
    {
        super.onDestroy();
    }
     
    @Override
    protected void onPause() 
    {
        super.onPause();
         
        // on pause turn off the flash
        turnOffFlash();
    }
     
    @Override
    protected void onRestart() 
    {
        super.onRestart();
    }
     
    @Override
    protected void onResume() 
    {
        super.onResume();
         
        // on resume turn on the flash
        //if(hasFlash)
        //{
        //    turnOnFlash();
        //}
    }
     
    @Override
    protected void onStart() 
    {
        super.onStart();
         
        // on starting the app get the camera params
        getCamera();
    }
     
    @Override
    protected void onStop() 
    {
        super.onStop();
         
        // on stop release the camera
        if (camera != null) 
        {
            camera.release();
            camera = null;
        }
    }
}
