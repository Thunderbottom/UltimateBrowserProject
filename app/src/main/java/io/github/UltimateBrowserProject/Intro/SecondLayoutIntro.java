package io.github.UltimateBrowserProject.Intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro2;

import io.github.UltimateBrowserProject.Activity.BrowserActivity;
import io.github.UltimateBrowserProject.Slides.FirstSlide2;
import io.github.UltimateBrowserProject.Slides.SecondSlide2;
import io.github.UltimateBrowserProject.Slides.ThirdSlide2;

public class SecondLayoutIntro extends AppIntro2 {
    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(new FirstSlide2(), getApplicationContext());
        addSlide(new SecondSlide2(), getApplicationContext());
        addSlide(new ThirdSlide2(), getApplicationContext());
    }

    private void loadMainActivity(){
        Intent intent = new Intent(this, BrowserActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDonePressed() {
        loadMainActivity();
    }

    public void getStarted(View v){
        loadMainActivity();
    }
}
