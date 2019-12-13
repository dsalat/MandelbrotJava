import java.awt.image.*;
import java.awt.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;


import org.bytedeco.ffmpeg.*;
import org.bytedeco.ffmpeg.avcodec.*;
import org.bytedeco.ffmpeg.avformat.*;
import org.bytedeco.ffmpeg.avutil.*;

import static org.bytedeco.ffmpeg.global.avcodec.*;
import static org.bytedeco.ffmpeg.global.avformat.*;
import static org.bytedeco.ffmpeg.global.avutil.*;

import org.bytedeco.javacv.*;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.lang.String.*;

public class Mandel 
{
	static int 	WIDTH = 1920;
	static int	HEIGHT = 1080;
	static int	VWIDTH = WIDTH*2;
	static int	VHEIGHT = HEIGHT*2;
	static		FFmpegFrameRecorder 		recorder;
	static		File				outfile;
	static		Frame				frame;
	static		BufferedImage			buffer;
	static 		int 				R[][] = new int[WIDTH][HEIGHT];
	static 		int 				G[][] = new int[WIDTH][HEIGHT];
	static 		int 				B[][] = new int[WIDTH][HEIGHT];
	static 		int 				VR[][] = new int[VWIDTH][VHEIGHT];
	static 		int 				VG[][] = new int[VWIDTH][VHEIGHT];
	static 		int 				VB[][] = new int[VWIDTH][VHEIGHT];
	static		Java2DFrameConverter		converter;

	public static void main (String args[]) throws Exception, IOException
	{
		int				i, j, x, y;

		if (args.length != 1)
		{
			System.out.println ("Usage: java demo <output file name>");
			System.exit(0);
		}

		
		outfile = new File(args[0]);
		
		
		recorder = new FFmpegFrameRecorder (outfile, WIDTH, HEIGHT);

		
		recorder.setVideoCodec (AV_CODEC_ID_WMV2);
		
	
		recorder.setFrameRate(30);

	
		recorder.setVideoOption ("preset", "ultrafast");

		
		recorder.setVideoBitrate(35000000);

		
		recorder.start();

		
		buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);

		
		converter = new Java2DFrameConverter();
		
		//MandelBrot
		
		int MAX_ITER = 500;
		double ZOOM = 400;
		double zx, zy, cX, cY, tmp;
		
	
		for(i = 1; i<250; i++)
		{
				
			for (y = 0; y < HEIGHT; y++) 
			{
				for (x = 0; x < WIDTH; x++) 
				{
					int iter = MAX_ITER;
					zx = zy = 0;
					cX = (x - (WIDTH/2)) / Math.pow(i,2) - 0.748; //* 1.5f;
					cY = (y - (HEIGHT/2)) / Math.pow (i,2)+ 0.1;//18215;

					
					while (zx * zx + zy * zy < 4 && iter > 0) {
						tmp = zx * zx - zy * zy + cX;
						zy = 2.0 * zx * zy + cY;
						zx = tmp;
						iter--;
					}
	               
	                
					buffer.setRGB(x, y, Color.HSBtoRGB(iter/(1.15f * i), 1F, 1F)  << 16 |  
					Color.HSBtoRGB(iter/(i * 1.1f), 1F, 1F) << 8 | 
					Color.HSBtoRGB(iter/(i * 1.05f), 1F, 1F));
					
				}
			}
			
			drawframe();
		}
		recorder.stop();
	}

	

	
	

	public static void drawframe () throws Exception, IOException
	{
		int 	x, y;
		int	pixcolor;	
		Color	pcolor;

	

		
		frame = converter.convert(buffer);
		
		
		recorder.record(frame, AV_PIX_FMT_ARGB);
	}
}