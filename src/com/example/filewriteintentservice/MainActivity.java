package com.example.filewriteintentservice;



import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements OnClickListener{
	
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //none
        Button button = (Button)this.findViewById(R.id.button_none);
        if(null != button) {
        	button.setOnClickListener(this);
        }
        button = null;
        
        //application area
        button = (Button)this.findViewById(R.id.button_application_area);
        if(null != button) {
        	button.setOnClickListener(this);
        }
        button = null;
        
        //external storage
        button = (Button)this.findViewById(R.id.button_external_storage);
        if(null != button) {
        	button.setOnClickListener(this);
        }
        
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, "Setting");
    	menu.add(Menu.NONE, Menu.FIRST + 1, Menu.NONE, "none");
    	menu.add(Menu.NONE, Menu.FIRST + 2, Menu.NONE, "none");
    	
        return super.onCreateOptionsMenu(menu);
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret = true;
		
		switch(item.getItemId()) {
		case Menu.FIRST:	//Setting
			break;
			
		case Menu.FIRST + 1:
			break;
		
		case Menu.FIRST + 2:
			break;
		
		default:
			ret = false;
			break;
		}
		
		
		return ret;
	}


	@Override
	public void onClick(View view) {
		Intent intent = new Intent(MainActivity.this,
				FileWriteIntentService.class);
		
		//ファイル名を設定
		intent.putExtra(FileWriteIntentService.EXTRA_FILE_NAME, "test01.txt");
		
		//書き込み内容を設定
		EditText editText = (EditText)this.findViewById(R.id.editText);
		if(null != editText) {
			intent.putExtra(FileWriteIntentService.EXTRA_WRITE_VALUE,
					editText.getText().toString());
		}
		
		//書き込みタイプの設定
		switch(view.getId()) {
		//none
		case R.id.button_none:
			intent.putExtra(FileWriteIntentService.EXTRA_WRITE_TYPE,
					FileWriteIntentService.TYPE_NONE);
			break;
		
		//application area
		case R.id.button_application_area:
			intent.putExtra(FileWriteIntentService.EXTRA_WRITE_TYPE,
					FileWriteIntentService.TYPE_APPLICATION_AREA);
			break;
			
		//external storage
		case R.id.button_external_storage:
			intent.putExtra(FileWriteIntentService.EXTRA_WRITE_TYPE,
					FileWriteIntentService.TYPE_EXTERNAL_STORAGE);
			break;
			
		default:
			return;
		}
		
		this.startService(intent);
	}
    
    
}
