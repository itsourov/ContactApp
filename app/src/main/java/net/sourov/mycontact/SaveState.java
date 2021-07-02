package net.sourov.mycontact;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveState {
    Context context;
    SharedPreferences sharedPreferences;

    public SaveState(Context context) {
        this.context = context;
        sharedPreferences=context.getSharedPreferences("preferance",Context.MODE_PRIVATE);
    }

    public void setState(boolean bvalue){
        SharedPreferences.Editor editor =sharedPreferences.edit();
        editor.putBoolean("bkey",bvalue);
        editor.apply();
    }
    public boolean getState(){
        return sharedPreferences.getBoolean("bkey",true);
    }
}
