package online.cagocapps.party_cat;


import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import online.cagocapps.party_cat.util.IabHelper;
import online.cagocapps.party_cat.util.IabResult;
import online.cagocapps.party_cat.util.Inventory;
import online.cagocapps.party_cat.util.Purchase;

import java.math.BigInteger;
import java.security.SecureRandom;



public class MainActivity extends AppCompatActivity {

    private final static String KEY_STRING =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhE+Y2kHCUCicZNbkS2waUaBolaSIIavSXi7Vdg7sw8B2LNNdZRPqSZEZx+INjYaaMZxSZpYOwm6+ap";
    private Button meowButton;
    private MediaPlayer sound;
    online.cagocapps.party_cat.util.IabHelper mHelper;
    static final String SKU_PREM = "drinkingmode";
    private String payload = new BigInteger(130, new SecureRandom()).toString(32);
    private Boolean drinkingGame;
    private Boolean partyMode;
    private TextView topText;
    private TextView bottomText;
    private Boolean mHelperSuccess = false;
    private Boolean direction = true;
    private AdView mAdView;
    private SharedPreferences sharedPref;
    private final static String TAG = "Main Activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        meowButton = (Button) findViewById(R.id.button);
        addListenerOnButton(meowButton);
        topText = (TextView) findViewById(R.id.top_text);
        bottomText = (TextView) findViewById(R.id.bottom_text);
        String license = KEY_STRING +
                "J+8CzeKp2TbNg38tSm8WaEqHduu/1KPwxiZJ6lm2KUXOpHj+zRm2/sEaVXtm6mUm2xPNR0mypcVoI3zxMoHeeq23Pxxp44ncAtXxA0gQ/o9W4zq0jyhNHitH/N"
                + getResources().getString(R.string.key);
        mHelper = new IabHelper(this,license);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener(){

            public void onIabSetupFinished(IabResult result) {
                mHelperSuccess = result.isSuccess();
                if (!mHelperSuccess){
                    Log.d("MainActivity", "Problem setting up In-app Billing: " + result);
                    return;
                }
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e){
                    Log.d(TAG, "can't query inventory");
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.prem_not_aval),Toast.LENGTH_SHORT).show();
                }
            }
        });
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            //@Override
            //public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                //if (key.equals(getString(R.string.premium_key))){
                    //mAdView.setVisibility(View.GONE);
                //}
            //}
       // };
        //sharedPref.registerOnSharedPreferenceChangeListener(listener);

        MobileAds.initialize(getApplicationContext(), getString(R.string.adMob_app_id));
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("76717E7B15FCA5425DF294B119AA246A").build();
        mAdView.loadAd(adRequest);
        //if (sharedPref.getBoolean(getString(R.string.premium_key), false))
            //mAdView.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        drinkingGame = sharedPref.getBoolean(getString(R.string.drinking_game_key), false);
        partyMode = sharedPref.getBoolean(getString(R.string.party_key), false);

    }


    public void addListenerOnButton(Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meowButton.setEnabled(false);
                meowButton.setBackgroundResource(R.drawable.open);

                int n = (int) (Math.random() * 15 + 1);
                if (drinkingGame) setDrinkingText(n);
                if (n == 1||n==2) sound = MediaPlayer.create(MainActivity.this, R.raw.meowone);
                else if (n == 3 || n==4) sound = MediaPlayer.create(MainActivity.this,R.raw.meowtwo);
                else if (n == 5 || n==6) sound = MediaPlayer.create(MainActivity.this,R.raw.meowthree);
                else if (n == 7 || n==8) sound = MediaPlayer.create(MainActivity.this,R.raw.meowfour);
                else if (n == 9 || n==10) sound = MediaPlayer.create(MainActivity.this,R.raw.meowfive);
                else if (n == 11 || n==12) sound = MediaPlayer.create(MainActivity.this, R.raw.meowsix);
                else if (n == 13 || n==14) sound = MediaPlayer.create(MainActivity.this, R.raw.meowseven);
                else {
                    sound = MediaPlayer.create(MainActivity.this, R.raw.dog);
                    meowButton.setBackgroundResource(R.drawable.pup);
                }

                sound.start();
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        goBack(sound);
                    }
                };
                Handler h = new Handler();
                h.postDelayed(r, sound.getDuration());
            }
        });
    }
    //Drinking game logic
    private void setDrinkingText(int n){
        topText.setVisibility(View.VISIBLE);
        bottomText.setVisibility(View.VISIBLE);
        if (partyMode){
            if (n == 1||n==2) {
                topText.setText(getResources().getString(R.string.pass));
                bottomText.setText(getResources().getString(R.string.one_drink));
            }
            else if (n == 3 || n==4) {
                direction = !direction;
                topText.setText(getResources().getString(R.string.reverse));
                bottomText.setText(getResources().getString(R.string.one_drink));
            }
            else if (n == 5 || n==6) {
                topText.setText(getResources().getString(R.string.keep));
                bottomText.setText(getResources().getString(R.string.one_drink));
            }
            else if (n == 7 || n==8) {
                topText.setText(getResources().getString(R.string.pass));
                int drinks = (int) (Math.random() * 4 + 2);
                bottomText.setText(getResources().getString(R.string.take) + " " +
                        drinks + " " + getResources().getString(R.string.drinks));
            }
            else if (n == 9 || n==10) {
                direction = !direction;
                topText.setText(getResources().getString(R.string.reverse));
                int drinks = (int) (Math.random() * 4 + 2);
                bottomText.setText(getResources().getString(R.string.take) + " " +
                        drinks + " " + getResources().getString(R.string.drinks));
            }
            else if (n == 11 || n==12) {
                topText.setText(getResources().getString(R.string.keep));
                int drinks = (int) (Math.random() * 4 + 2);
                bottomText.setText(getResources().getString(R.string.take) + " " +
                        drinks + " " + getResources().getString(R.string.drinks));
            }
            else if (n == 13 || n==14) {
                topText.setText(getResources().getString(R.string.pass));
                bottomText.setText(getResources().getString(R.string.one_drink));
            }
            else {
                topText.setText(getResources().getString(R.string.anyone));
                bottomText.setText(getResources().getString(R.string.finish));
            }
        }
        else{
            if (n == 1||n==2) {
                topText.setText(getResources().getString(R.string.pass));
            }
            else if (n == 3 || n==4) {
                direction = !direction;
                topText.setText(getResources().getString(R.string.reverse));
            }
            else if (n == 5 || n==6) {
                topText.setText(getResources().getString(R.string.keep));
            }
            else if (n == 7 || n==8) {
                topText.setText(getResources().getString(R.string.pass));
                bottomText.setText(getResources().getString(R.string.one_drink));
            }
            else if (n == 9 || n==10) {
                direction = !direction;
                topText.setText(getResources().getString(R.string.reverse));
                bottomText.setText(getResources().getString(R.string.one_drink));
            }
            else if (n == 11 || n==12) {
                topText.setText(getResources().getString(R.string.keep));
                bottomText.setText(getResources().getString(R.string.one_drink));
            }
            else if (n == 13 || n==14) {
                topText.setText(getResources().getString(R.string.pass));
                bottomText.setText(getResources().getString(R.string.one_drink));
            }
            else {
                topText.setText(getResources().getString(R.string.anyone));
                bottomText.setText(getResources().getString(R.string.chug));
            }
        }
    }
    //Time delayed routine that resets the screen
    private void goBack(MediaPlayer soundToStop) {
        soundToStop.stop();
        soundToStop.release();
        topText.setText(null);
        bottomText.setText(null);
        topText.setVisibility(View.INVISIBLE);
        bottomText.setVisibility(View.INVISIBLE);
        meowButton.setEnabled(true);
        meowButton.setBackgroundResource(R.drawable.closed);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            Intent startSettingsActivity = new Intent(this, Settings.class);
            startActivity(startSettingsActivity);
            return true;
        }
        else if (id == R.id.purchase)

            try {
                mHelper.launchPurchaseFlow(this, SKU_PREM, 10001, mPurchaseFinishedListener, payload);
            } catch (IabHelper.IabAsyncInProgressException e) {
                Log.d(TAG, "Purchase Failed IabAsyncInProgress Exception");
                Toast.makeText(this,getString(R.string.prem_not_aval),Toast.LENGTH_SHORT).show();
            } catch (IllegalStateException e) {
                Log.d(TAG, "Purchase Failed Illegal State Exception");
                Toast.makeText(this,getString(R.string.prem_not_aval),Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e){
                Log.d(TAG, "Purchase Failed Null Pointer Exception");
                Toast.makeText(this,getString(R.string.prem_not_aval),Toast.LENGTH_SHORT).show();
            }
        return super.onOptionsItemSelected(item);
    }
    //Turn on premium content
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase){
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
            if (result.isFailure()){
                Toast.makeText(MainActivity.this, getResources().getString(R.string.purchase_fail), Toast.LENGTH_SHORT).show();
            }
            else if (purchase.getSku().equals(SKU_PREM) && purchase.getDeveloperPayload().equals(payload) ){
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getString(R.string.premium_key), true);
                editor.commit();
                //mAdView.setVisibility(View.GONE);
            }
        }
    };
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener =
            new IabHelper.QueryInventoryFinishedListener() {
                public void onQueryInventoryFinished(IabResult result, Inventory inventory){
                    if (mHelper == null) return;
                    if (result.isFailure()){
                        Toast.makeText(MainActivity.this,getResources().getString(R.string.prem_not_aval),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (inventory.hasPurchase(SKU_PREM)){
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(getString(R.string.premium_key), true);
                        editor.commit();
                        //mAdView.setVisibility(View.GONE);
                    }
                    else if (!inventory.hasPurchase(SKU_PREM)){
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(getString(R.string.premium_key), false);
                        editor.commit();
                        //mAdView.setVisibility(View.VISIBLE);
                    }

                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.meow_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++){
            if (menu.getItem(i).getItemId() == R.id.purchase){
                menu.getItem(i).setEnabled(mHelperSuccess);
                if (sharedPref.getBoolean(getString(R.string.premium_key), false))
                    menu.getItem(i).setEnabled(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mHelper != null) mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            mHelper = null;
        } catch (IllegalArgumentException e){
            mHelper = null;
        }
        mHelper = null;
    }
}
