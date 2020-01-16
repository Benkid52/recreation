package factory;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import pojo.Slip;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Properties;
import java.util.Random;

/**
 * @author ylr
 */
public class SlipFactory {
    private SlipFactory() {};

    public static Slip getInstance() throws IOException {
        Slip slip = new Slip();
        Properties property = new Properties();

        //读取配置文件
        FileInputStream in = new FileInputStream("pictureConfig.properties");
        BufferedInputStream buff = new BufferedInputStream(in);
        property.load(buff);
        buff.close();
        in.close();

        //属性设置
        slip.setSmallWidth((Integer)property.get("slip.smallWidth"));
        slip.setSmallHeight((Integer)property.get("slip.smallHeight"));
        slip.setBigWidth((Integer)property.get("slip.bigWidth"));
        slip.setBigHeight((Integer)property.get("slip.bigHeight"));
        slip.setCircleR((Integer)property.get("slip.circleR"));
        slip.setGood((Integer)property.get("slip.good"));

        //随机坐标
        Random rand = new Random();
        int randX = rand.nextInt(slip.getBigWidth() - slip.getSmallWidth() - (Integer)property.get("slip.bound"));
        int randY = rand.nextInt(slip.getBigHeight() - slip.getSmallWidth());
        slip.setRandX(randX);
        slip.setRandY(randY);

        //随机图片
        int picture = rand.nextInt((Integer)property.get("slip.num"));
        slip.setUrl((String)property.get("slip.url") + "/code" + picture + ".png");

        //图片base64
        String[] base64 = getBase64(slip);
        slip.setSmallBase64(base64[1]);
        slip.setBigBase64(base64[0]);

        return slip;
    }

    //获取图片base64
    private static String[] getBase64(Slip slip) throws IOException {
        //大图
        File f = new File(slip.getUrl());
        BufferedImage big = new BufferedImage(slip.getBigWidth(), slip.getBigHeight(), BufferedImage.TYPE_4BYTE_ABGR);      //缩放后的图片
        BufferedImage original = ImageIO.read(f);      //原始图片
        Graphics2D g = big.createGraphics();
        g.drawImage(original, 0, 0, slip.getBigWidth(), slip.getBigHeight(), null);
        g.dispose();

        //小图
        int[][] smallData = getSmallData(slip);
        BufferedImage small = new BufferedImage(slip.getSmallWidth(), slip.getSmallHeight(), BufferedImage.TYPE_4BYTE_ABGR);

        return cutPicture(slip, big, small, smallData);
    }

    //获取小图样式
    private  static int[][] getSmallData(Slip slip) {
        int[][] smallData = new int[slip.getSmallWidth()][slip.getSmallHeight()];
        int x = slip.getSmallWidth() - slip.getCircleR();       //矩形宽度
        int y = slip.getSmallHeight() - slip.getCircleR();      //矩形高度
        int r2 = slip.getCircleR() * slip.getCircleR();     //半径的平方

        //16种不同状态
        Random rand = new Random();
        int state = rand.nextInt(16);
        int oX = state < 8 ? slip.getCircleR() : x;     //x圆心在左还是右
        int oY = (state % 8) < 4 ? slip.getCircleR() : y;       //y圆心在上还是下
        int oXy = oY == slip.getCircleR() ? slip.getCircleR() + x / 2 : x / 2;      //y圆心的x坐标
        int oYx = oX == slip.getCircleR() ? slip.getCircleR() + y / 2 : y / 2;      //x圆心的y坐标

        //0表示无颜色，1表示有颜色
        for(int i = 0; i < slip.getSmallWidth(); i++) {
            for(int j = 0; j < slip.getSmallHeight(); j++) {
                //到圆心距离
                int dX = (i - oX) * (i - oX) + (j - oXy) * (j - oXy);
                int dY = (i - oYx) * (i - oYx) + (j - oY) * (j - oY);
                switch (state) {
                    case 0:     //x左凹，y上凹
                        if(isOne(i <= oX, j <= oY, dX <= r2, dY <= r2)) smallData[i][j] = 1;
                        else smallData[i][j] = 0;
                        break;
                    case 1:     //x左凹，y上凸
                        if(isOne(i <= oX, j <= oY - slip.getCircleR(), dX < r2, (dY > r2 && j > oY - slip.getCircleR() && j < oY))) smallData[i][j] = 1;
                        else smallData[i][j] = 0;
                        break;
                    case 2:     //x左凸，y上凹
                        if(isOne(i <= oX - slip.getCircleR(), j <= oY, (dX > r2 && i > oX - slip.getCircleR() && i < oX), dY < r2)) smallData[i][j] = 1;
                        else smallData[i][j] = 0;
                        break;
                    case 3:     //x左凸，y上凸
                        if(isOne(i <= oX - slip.getCircleR(), j <= oY - slip.getCircleR(), (dX > r2 && i > oX - slip.getCircleR() && i < oX), (dY > r2 && j > oY - slip.getCircleR() && j < oY))) smallData[i][j] = 1;
                        else smallData[i][j] = 0;
                        break;
                    case 4:     //x左凹，y下凹
                        if(isOne(i <= oX, j >= oY, dX <= r2 , dY <= r2)) smallData[i][j] = 1;
                        else smallData[i][j] = 0;
                        break;
                    case 5:     //x左凹，y下凸
                        if(isOne(i <= oX, j >= oY + slip.getCircleR(), dX < r2, (dY > r2 && j > oY  && j < oY + slip.getCircleR()))) smallData[i][j] = 1;
                        else smallData[i][j] = 0;
                        break;
                    case 6:     //x左凸，y下凹
                        if(isOne(i <= oX - slip.getCircleR(), j >= oY , (dX > r2 && i > oX - slip.getCircleR() && i < oX), dY < r2)) smallData[i][j] = 1;
                        else smallData[i][j] = 0;
                        break;
                    case 7:     //x左凸，y下凸
                        if(isOne(i <= oX - slip.getCircleR(), j >= oY + slip.getCircleR(), (dX > r2 && i > oX - slip.getCircleR() && i < oX), (dY > r2 && j > oY && j < oY + slip.getCircleR()))) smallData[i][j] = 1;
                        else smallData[i][j] = 0;
                        break;
                    case 8:     //x右凹，y上凹
                        if(isOne(i >= oX, j <= oY, dX <= r2, dY <= r2)) smallData[i][j] = 1;
                        else smallData[i][j] = 0;
                        break;
                    case 9:     //x右凹，y上凸
                        if(isOne(i >= oX, j <= oY - slip.getCircleR(), dX < r2, (dY > r2 && j > oY - slip.getCircleR() && j < oY))) smallData[i][j] = 1;
                        else smallData[i][j] = 0;
                        break;
                    case 10:    //x右凸，y上凹
                        if(isOne(i >= oX + slip.getCircleR(), j <= oY, (dX > r2 && i > oX && i < oX + slip.getCircleR()), dY < r2)) smallData[i][j] = 1;
                        else smallData[i][j] = 0;
                        break;
                    case 11:    //x右凸，y上凸
                        if(isOne(i >= oX + slip.getCircleR(), j <= oY - slip.getCircleR(), (dX > r2 && i > oX && i < oX + slip.getCircleR()), (dY > r2 && j > oY - slip.getCircleR() && j < oY))) smallData[i][j] = 1;
                        else smallData[i][j] = 0;
                        break;
                    case 12:    //x右凹，y下凹
                        if(isOne(i >= oX, j >= oY, dX <= r2, dY <= r2)) smallData[i][j] = 1;
                        else smallData[i][j] = 0;
                        break;
                    case 13:    //x右凹，y下凸
                        if(isOne(i >= oX, j >= oY + slip.getCircleR(), dX < r2, (dY > r2 && j > oY  && j < oY + slip.getCircleR()))) smallData[i][j] = 1;
                        else smallData[i][j] = 0;
                        break;
                    case 14:    //x右凸，y下凹
                        if(isOne(i >= oX + slip.getCircleR(), j >= oY, (dX > r2 && i > oX && i < oX + slip.getCircleR()), dY < r2)) smallData[i][j] = 1;
                        else smallData[i][j] = 0;
                        break;
                    case 15:    //x右凸，y下凸
                        if(isOne(i >= oX + slip.getCircleR(), j >= oY + slip.getCircleR(), (dX > r2 && i > oX && i < oX + slip.getCircleR()), (dY > r2 && j > oY && j < oY + slip.getCircleR()))) smallData[i][j] = 1;
                        else smallData[i][j] = 0;
                        break;
                }
            }
        }

        return smallData;
    }

    //判断smallData[][]的值是否为1
    private static boolean isOne(boolean b0, boolean b1, boolean b2, boolean b3) {
        boolean res = true;
        if(b0 || b1 || b2 || b3) res = false;
        return res;
    }

    //抠图
    private static String[] cutPicture(Slip slip, BufferedImage obig, BufferedImage small, int[][] smallData) throws IOException {
        String base64Big = null;        //大图base64
        String base64Small = null;      //小图base64

        BufferedImage big = new BufferedImage(slip.getBigWidth(), slip.getBigHeight(), BufferedImage.TYPE_4BYTE_ABGR);      //抠后大图
        for(int i = 0; i < slip.getBigWidth(); i++) {
            for(int j = 0; j < slip.getBigHeight(); j++) {
                big.setRGB(i, j, obig.getRGB(i, j));
                int randX = slip.getRandX();
                int randY = slip.getRandY();
                if(i - randX >= 0 && j - randY >= 0 && i - randX < slip.getSmallWidth() && j - randY < slip.getSmallHeight()) {
                    if(smallData[i - randX][j - randY] == 1) {
                        big.setRGB(i, j, 0xAA000000);
                        small.setRGB(i - randX, j - randY, obig.getRGB(i, j));
                    } else {
                        small.setRGB(i - randX, j - randY, 0x00FFFFFF);
                    }
                }
            }
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(big, "png", bos);
        base64Big = Base64.encode(bos.toByteArray());
        bos.close();
        bos = new ByteArrayOutputStream();
        ImageIO.write(small, "png", bos);
        base64Small = Base64.encode(bos.toByteArray());
        bos.close();

        String[] res = {base64Big, base64Small};
        return res;
    }
}
