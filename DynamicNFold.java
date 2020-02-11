package Skin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class DynamicNFold {
    public static int start=0,end,partiton;
    public static int[][][] skin = new int[256][256][256];
    public static   int[][][] nonSkin = new int[256][256][256];

    public static   double[][][] probability = new double[256][256][256];



    public static void Test(File bmpFile[],File jpegFile[]) throws IOException
    {

        File [] testJpegFiles =new File[partiton];
        File [] testBmpFiles=new File[partiton];
        int size=0;
        for(int i=start;i<end;i++)
       {
           testJpegFiles[size]=jpegFile[i];
           testBmpFiles[size]=bmpFile[i];
           size++;
       }


        for(int j=0;j<testBmpFiles.length;j++)
        {
            System.out.println(testBmpFiles[j].getName()+"\t"+testJpegFiles[j].getName());
        }

   //     System.out.println("NUMBER = "+testJpegFiles.length);

     //   System.out.println("NUMBER = "+testBmpFiles.length);
        double sum=0;
        int total=0;

        for (int k = 0; k < testJpegFiles.length; k++) {


            BufferedImage image = ImageIO.read(testJpegFiles[k]);
            BufferedImage jpgImage = ImageIO.read(testBmpFiles[k]);

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


           /* String  s=Integer.toString(k);
            s+=".jpg";
            */

           String name=testJpegFiles[k].getName();
            File outputfile = new File("TestResult/"+name);
            ImageIO.write(image, "jpg", outputfile);
            //System.out.println("win = "+win);
          //  System.out.println("Accuracy = "+win*100/(w*h));
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



    public static void main(String[]args) throws IOException {
        File path = new File("Mask");
        File [] files = path.listFiles();
        File jpgPath = new File("Bask");
        File [] jpgFiles = jpgPath.listFiles();



     //   System.out.println("NUMBER = "+files.length);

     //   System.out.println("NUMBER = "+jpgFiles.length);




        Map< File,File> hm =new HashMap<>();
        for(int i=0;i<files.length;i++)
        {
            hm.put(files[i],jpgFiles[i]);

        }

        File[] bmpFile=new File [555];
        File[] jpegFile=new File [555];
        int itr=0;


        List keys = new ArrayList(hm.keySet());
        Collections.shuffle(keys);


        Set< Map.Entry< File,File> > st = hm.entrySet();
        for (Map.Entry< File,File> me:st)
        {
         //   System.out.print(me.getKey()+":");
           // System.out.println(me.getValue());
            bmpFile[itr]=me.getKey();
            jpegFile[itr]=me.getValue();
            itr++;


        }

       /* for(int j=0;j<jpegFile.length;j++)
        {
            System.out.println(bmpFile[j].getName()+"\t"+jpegFile[j].getName());
        }*/
        Scanner sc=new Scanner(System.in);
        System.out.print("Number of Division = ");
        int div=sc.nextInt();


        partiton=bmpFile.length/div;
        end=partiton+start;

        while(div!=0)
        {
            System.out.println("Start = "+start+"\tEnd = "+end+"\tpart = "+partiton);

            for (int i = 0; i <start ; i++){


                BufferedImage image = ImageIO.read(files[i]);
                BufferedImage jpgImage =ImageIO.read(jpgFiles[i]);


                marchThroughImage(image,jpgImage);

            }

            for (int i = end; i <bmpFile.length ; i++){


                BufferedImage image = ImageIO.read(files[i]);
                BufferedImage jpgImage =ImageIO.read(jpgFiles[i]);


                marchThroughImage(image,jpgImage);

            }

            System.out.println("end process");

            Probability();
            System.out.println("end probability");

            Test(bmpFile ,jpegFile);
            System.out.println("See result");




            start=end;
            end=end+partiton;
            div--;
        }

    }
}
