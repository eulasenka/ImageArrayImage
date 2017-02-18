import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ImageArrayImage {
	
	private static BufferedImage image;
	//Image width & heigh
	private static int w;
	private static int h;
	//Contains aRGB color for each region
	private static int[] colors = new int[4];
	//Contains aRGB color for each pixel
	private static int[] dataBuffInt;
	/*Neural network result
	 * Contains aRGB color for each pixel
	 */
	private static int[] resultArray;
	//Size of dataBuffInt
	private static int size;
	/*RGB array for each pixel
     * We need it to create ImageIcon
     * to show the result on screen
     */
    private static int[] rgbArray;
    
	
	//ImageIcon create
	public static Image getImageFromArray(int[] pixels, int width, int height)
	  {
	    BufferedImage image =
	        new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	    WritableRaster raster = (WritableRaster) image.getData();
	    raster.setPixels(0, 0, width, height, pixels);
	    image.setData(raster);
	    return image;
	  }
	
	/**
	 *
	 * @throws Exception
	 */
	public static void getArrayFromImage(String imageName) throws Exception {
		image = ImageIO.read(new File(imageName));
		w = image.getWidth();
	    h = image.getHeight();
	    
	    //Initialization
	    colors[0] = 0;
	    colors[1] = 0;
	    colors[2] = 0;
	    colors[3] = 0;
	    
	    dataBuffInt = image.getRGB(0, 0, w, h, null, 0, w);
	    size = dataBuffInt.length;
	    
	    resultArray = new int[size];
	    
	    //Find our four aRGB colors
	    int j=0;
	    for(int i=0; i<43264; i++)
	    {
	    	if(dataBuffInt[i] != colors[0] &&
	    	   dataBuffInt[i] != colors[1] &&
	    	   dataBuffInt[i] != colors[2] &&
	    	   dataBuffInt[i] != colors[3])
	    	{
	    		colors[j] = dataBuffInt[i];
	    		j++;
	    	}
	    }
	    
	    /*
	     * Show it
	     * OPTIONALLY
	     */
	    System.out.println("Number of pixels: " + size);
	    
	    for(int i=0; i<4; i++)
	    {
	    	System.out.println("Color: " + colors[i]);
	    }
	}
	
	public static void createAndSaveImageInFile(int[] pixelArray, String imageName) throws Exception
	{
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		final int[] a = ((DataBufferInt)bi.getRaster().getDataBuffer()).getData();
	    System.arraycopy(pixelArray, 0, a, 0, pixelArray.length);
	    File outputfile = new File(imageName);
	    ImageIO.write(bi, "png", outputfile);
	}
	
	/**
	 * 
	 * @return number of region, which is in range [1..4]
	 */
	public static int getRegion(int x, int y)
	{
		if(y==0)
		{
			if(dataBuffInt[x] == colors[0])
				return 1;
			else if (dataBuffInt[x] == colors[1])
				return 3;
			else if (dataBuffInt[x] == colors[2])
				return 4;
			else if (dataBuffInt[x] == colors[3])
				return 2;
			else
				return -1;
		}
		else
		{
			if(dataBuffInt[(size/w)*(y-1)+x] == colors[0])
				return 1;
			else if (dataBuffInt[(size/w)*(y-1)+x] == colors[1])
				return 3;
			else if (dataBuffInt[(size/w)*(y-1)+x] == colors[2])
				return 4;
			else if (dataBuffInt[(size/w)*(y-1)+x] == colors[3])
				return 2;
			else
				return -1;
		}
	}
	
	/**
	 * @return index of resultArray if succeed; -1 if failed
	 */
	public static int setPixel(int x, int y, int regionNumber)
	{
		if (regionNumber == 1)
			resultArray[w*y+x] = colors[0];
		
		else if (regionNumber == 2)
			resultArray[w*y+x] = colors[3];
		
		else if (regionNumber == 3)
			resultArray[w*y+x] = colors[1];
		
		else if (regionNumber == 4)
			resultArray[w*y+x] = colors[2];
		
		else
			return -1;
		
		return w*y+x;
	}
	
	/**
	 * Get RGB array from aRGB array.
	 * We need it to create an ImageIcon
	 * @return rgbArray
	 */
	public static int[] getRGBArray(int[] aRGBArray)
	{
		rgbArray = new int[3*size];
    
		int j=0;
		for(int i=0; i<size; i++)
		{
			Color c = new Color(aRGBArray[i]);
    	
			rgbArray[j++] = c.getRed(); 
			rgbArray[j++] = c.getGreen();
			rgbArray[j++] = c.getBlue();
		}
		
		return rgbArray;
	}
	
	public static void main(String... args) throws Exception {
		
		getArrayFromImage("origin.bmp");
		
		getRGBArray(dataBuffInt);
	    
		
		//Show result on screen
	    JFrame jf = new JFrame();
	    JLabel jl = new JLabel();
	    
	    ImageIcon ii = new ImageIcon(getImageFromArray(rgbArray, w, h));
	    jl.setIcon(ii);
	    jf.add(jl);
	    jf.pack();
	    jf.setVisible(true);
	    
	    
	    createAndSaveImageInFile(dataBuffInt, "result.png");
	    
	    
	    System.out.println("Test: ");
	    System.out.println("getRegion(0,0): " + getRegion(0,0));
	    System.out.println("getRegion(100,100): " + getRegion(100,100));
	    System.out.println("getRegion(50,100): " + getRegion(50,100));
	    System.out.println("getRegion(200,200): " + getRegion(200,200));
	    System.out.println("getRegion(140,100): " + getRegion(140,100));
	    System.out.println("getRegion(200,100): " + getRegion(200,100));
	}	
}
