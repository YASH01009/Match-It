package program.matchit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PlayAgain extends AppCompatActivity {

    Button playAgain;
    TextView wrongAnsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_again);

        //Initialize
        playAgain = (Button) findViewById(R.id.playAgainButton);
        wrongAnsText = (TextView)findViewById(R.id.wrongAns);

        //play again button onclick listener
        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlayAgain.this, SecondActivity.class);
                startActivity(intent);
                finish();
            }
        });

        playAgain.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
        wrongAnsText.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
