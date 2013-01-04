package com.torianin.androidapp9;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraActivity extends Activity {
	private static final int CAMERA_PIC_REQUEST = 1337;
	Button createFoto;
	
	Button filtr1;
	Button filtr2;
	Button filtr3;
	Button filtr4;
	
	Button saveImage;
	
	Bitmap orginal;
	Bitmap thumbnail;
	ImageView image;

	public Bitmap addFilter(Bitmap bitmap, int[] filter, double factor,
			double bias) {
		int width, height;
		height = bitmap.getHeight();
		width = bitmap.getWidth();
		int red = 0, green = 0, blue = 0;
		int[] pixel = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		Bitmap bmpBlurred = Bitmap.createBitmap(width, height,
				bitmap.getConfig());
		Canvas canvas = new Canvas(bmpBlurred);
		canvas.drawBitmap(bitmap, 0, 0, null);
		for (int i = 1; i < width - 1; i++) {
			for (int j = 1; j < height - 1; j++) {
				pixel[0] = bitmap.getPixel(i - 1, j - 1);
				pixel[1] = bitmap.getPixel(i - 1, j);
				pixel[2] = bitmap.getPixel(i - 1, j + 1);
				pixel[3] = bitmap.getPixel(i, j - 1);
				pixel[4] = bitmap.getPixel(i, j);
				pixel[5] = bitmap.getPixel(i, j + 1);
				pixel[6] = bitmap.getPixel(i + 1, j - 1);
				pixel[7] = bitmap.getPixel(i + 1, j);
				pixel[8] = bitmap.getPixel(i + 1, j + 1);
				for (int k = 0; k < 9; k++) {
					red = red + Color.red(pixel[k]) * filter[k];
				}
				for (int k = 0; k < 9; k++) {
					green = green + Color.green(pixel[k]) * filter[k];
				}
				for (int k = 0; k < 9; k++) {
					blue = blue + Color.blue(pixel[k]) * filter[k];
				}
				red = Math.min(Math.max((int) (factor * red + bias), 0), 255);
				green = Math.min(Math.max((int) (factor * green + bias), 0),
						255);
				blue = Math.min(Math.max((int) (factor * blue + bias), 0), 255);
				bmpBlurred.setPixel(i, j, Color.rgb(red, green, blue));
				red = green = blue = 0;
			}
		}
		return bmpBlurred;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		createFoto = (Button) findViewById(R.id.button1);
		filtr1 = (Button) findViewById(R.id.button2);
		filtr2 = (Button) findViewById(R.id.button3);
		filtr3 = (Button) findViewById(R.id.button4);
		filtr4 = (Button) findViewById(R.id.button5);
		saveImage = (Button) findViewById(R.id.button6);
	
		image = (ImageView) findViewById(R.id.imageView1);
		
		createFoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createFoto.setText("Zrób następne zdjęcie");
				filtr1.setVisibility(1);
				filtr2.setVisibility(1);
				filtr3.setVisibility(1);
				filtr4.setVisibility(1);
				saveImage.setVisibility(1);
				image.setVisibility(1);
				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
			}
		});
	
		filtr1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int filter[] =
					{
					    -1, -1, -1,
					    -1,  9, -1,
					    -1, -1, -1
					};
				thumbnail = addFilter(orginal, filter, 1.0 , 0.0);
				image.setImageBitmap(thumbnail);
			}
		});
		
		filtr2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int filter[] =
					{
					    -1, -1,  0,
					    -1,  0,  1,
					     0,  1,  1
				};
				thumbnail = addFilter(orginal, filter, 1.0 , 128.0);
				image.setImageBitmap(thumbnail);
			}
		});
		
		filtr3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int filter[] =
					{
						   1, 1, 1,
						    1, 1, 1,
						    1, 1, 1
				};
				thumbnail = addFilter(orginal, filter, (1.0 / 9.0) , 0.0);
				image.setImageBitmap(thumbnail);
			}
		});
		
		filtr4.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int filter[] =
					{
					     0,  0, -1,  0,  0,
					     0,  0, -1,  0,  0,
					     0,  0,  4,  0,  0,
					     0,  0, -1,  0,  0,
					     0,  0, -1,  0,  0,
				};
				thumbnail = addFilter(orginal, filter, 1 , 128.0);
				image.setImageBitmap(thumbnail);
			}
		});
		
		saveImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					   File sdCard = Environment.getExternalStorageDirectory();
					   File dir = new File (sdCard.getAbsolutePath() + "/Fotos/");
					   dir.mkdirs();
					   Calendar c = Calendar.getInstance(); 
					   int seconds = c.get(Calendar.SECOND);
					   int minute = c.get(Calendar.MINUTE);
					   int hour = c.get(Calendar.HOUR);
					   int dayofyear = c.get(Calendar.DAY_OF_YEAR);
					   String name = ""+dayofyear+hour+minute+seconds+".png";
					   File file = new File(dir, name);
				       FileOutputStream out = new FileOutputStream(file);
				       thumbnail.compress(Bitmap.CompressFormat.PNG, 90, out);
				} catch (Exception e) {
				       e.printStackTrace();
				}
			}
		});

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_PIC_REQUEST) {
			thumbnail = (Bitmap) data.getExtras().get("data");
			orginal = (Bitmap) data.getExtras().get("data");
			image.setImageBitmap(thumbnail);
		}
	}

}
