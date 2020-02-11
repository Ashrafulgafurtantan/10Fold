package Skin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static int[][][] skin = new int[256][256][256];
    public static   int[][][] nonSkin = new int[256][256][256];

    public static   double[][][] probability = new double[256][256][256];

  /*  public static void Test(BufferedImage image) throws IOException
    {
        int w = image.getWidth();
        int h = image.getHeight();
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int pixel = image.getRGB(j, i);
                int alpha = (pixel >> 24) & 0xff;
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;
                //skin
                if(probability[red][green][blue]>0.5) {

                    pixel=alpha<<24|(red<<16)|(green<<8)|blue;
                    image.setRGB(j,i,pixel);

                }
                else {
                    red=255;
                    green=255;
                    blue=255;
                    pixel=alpha<<24|(red<<16)|(green<<8)|blue;
                    image.setRGB(j,i,pixel);

                }

            }
        }
        File outputfile = new File("saved.jpg");
        ImageIO.write(image, "jpg", outputfile);

    }*/

   public static void Test() throws IOException
    {
        File path = new File("TestBask");
        File [] files = path.listFiles();
        File jpgPath = new File("TestMask");
        File [] jpgFiles = jpgPath.listFiles();


        System.out.println("NUMBER = "+files.length);

        System.out.println("NUMBER = "+jpgFiles.length);
       double sum=0;
        int total=0;

        for (int k = 0; k < files.length; k++) {


            BufferedImage image = ImageIO.read(files[k]);
            BufferedImage jpgImage = ImageIO.read(jpgFiles[k]);

            //For Mask
         //   System.out.println(image.getWidth()+","+image.getHeight()+","+jpgImage.getWidth()+","+jpgImage.getHeight());



            int w = image.getWidth();
            int h = image.getHeight();
            double win=0;
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    int pixel = image.getRGB(j, i);
                    int alpha = (pixel >> 24) & 0xff;
                    int red = (pixel >> 16) & 0xff;
                    int green = (pixel >> 8) & 0xff;
                    int blue = (pixel) & 0xff;


                    int pixelz = jpgImage.getRGB(j, i);
                    int alphaz = (pixelz >> 24) & 0xff;
                    int redz = (pixelz >> 16) & 0xff;
                    int greenz = (pixelz >> 8) & 0xff;
                    int bluez = (pixelz) & 0xff;


                    //skin
                    if (probability[red][green][blue] > 3.4) {

                        pixel = alpha << 24 | (red << 16) | (green << 8) | blue;
                        image.setRGB(j, i, pixel);

                        if(redz<253 || greenz<253 || bluez<253)  win++;

                    } else {
                        red = 255;
                        green = 255;
                        blue = 255;
                        pixel = alpha << 24 | (red << 16) | (green << 8) | blue;
                        image.setRGB(j, i, pixel);

                        if(redz>=253 || greenz>=253 || bluez>=253) win++;

                    }

                }
            }
          //  File file = new File("/home/userName/Documents/results.txt");


            String  s=Integer.toString(k);
            s+=".jpg";
            File outputfile = new File("TestResult/"+s);
            ImageIO.write(image, "jpg", outputfile);
            //System.out.println("win = "+win);
            System.out.println("Accuracy = "+win*100/(w*h));
            sum+=(win*100)/(w*h);
            total++;

        }
        System.out.println("Total= "+sum/total);

    }

    public static void Probability() throws IOException
    {
        double sumSkin=0;
        double sumNonSkin=0;
        for(int i=0;i<256;i++)
        {
            for(int j=0;j<256;j++)
            {
                for(int k=0;k<256;k++)
                {
                    sumSkin+=skin[i][j][k];
                    sumNonSkin+=nonSkin[i][j][k];

                }
            }
        }
        System.out.println("Skin = "+sumSkin);
        System.out.println("Non = "+sumNonSkin);

        double[][][] probabilityOfSkin = new double[256][256][256];
        double[][][] probabilityOfNonSkin = new double[256][256][256];
        FileOutputStream fout=new FileOutputStream("testout.txt");
        for(int i=0;i<256;i++)
        {
            for(int j=0;j<256;j++)
            {
                for(int k=0;k<256;k++)
                {

                    if(nonSkin[i][j][k]==0){

                    }
                    else {
                        probability[i][j][k] = ((skin[i][j][k] / sumSkin) / (nonSkin[i][j][k] / sumNonSkin));
                    }
                }
            }
        }

        System.out.println("end the whole pro");

        fout.close();

    }

    static void printPixelARGB(int pixel,int x) {
        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;
        // System.out.println("argb: " + alpha + ", " + red + ", " + green + ", " + blue);
        //Skin

        //NonSkin
        if(red>254 || green>254 || blue>254){
            alpha = (x >> 24) & 0xff;
            red = (x >> 16) & 0xff;
            green = (x >> 8) & 0xff;
            blue = (x) & 0xff;
            nonSkin[red][green][blue]++;

        }
        //skin
        else {

            alpha = (x >> 24) & 0xff;
            red = (x >> 16) & 0xff;
            green = (x >> 8) & 0xff;
            blue = (x) & 0xff;

            skin[red][green][blue]++;
        }
    }
    static void show()
    {
        for(int i=0;i<256;i++)
        {
            for(int j=0;j<256;j++)
            {
                for(int k=0;k<256;k++)
                {

                }
                System.out.println("");
            }
            System.out.println("");

        }
        System.out.println("");


    }
    static void marchThroughImage(BufferedImage image,BufferedImage jpgImage) throws IOException {

        int w = image.getWidth();
        int h = image.getHeight();


        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int pixel = image.getRGB(j, i);
                int x=jpgImage.getRGB(j,i);
                printPixelARGB(pixel,x);
            }
        }
    }

    public static void main(String[] args) {

        try {
            File path = new File("Mask");
            File [] files = path.listFiles();
            File jpgPath = new File("Bask");
            File [] jpgFiles = jpgPath.listFiles();


            System.out.println("NUMBER = "+files.length);

            System.out.println("NUMBER = "+jpgFiles.length);

            for (int i = 0; i < files.length; i++){
                // if (files[i].isFile()){ //this line weeds out other directories/folders
                // imageCollection.add((files[i]));

                BufferedImage image = ImageIO.read(files[i]);
                BufferedImage jpgImage =ImageIO.read(jpgFiles[i]);

                //   ImageIO.write(image,"bmp",new File("LOL.bmp"));

                // ImageIO.write(jpgImage,"jpg",new File("BOL.jpg"));

                marchThroughImage(image,jpgImage);

            }
            System.out.println("end process");

            Probability();
            System.out.println("end probability");
            //  show();
           // File f=new File("people.jpg");
          //  BufferedImage   image=ImageIO.read(f);
            //System.out.println("end reading image");
            Test();
            System.out.println("See result");



        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
