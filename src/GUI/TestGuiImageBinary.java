package GUI;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

public class TestGuiImageBinary {

    public static void main(String[] args) throws IOException {

        BufferedImage bImage = ImageIO.read(new File("C:\\Users\\danilodjurovic\\Desktop\\MasterTeam Brojacica\\src\\sample.jpg"));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "jpg", bos);
        byte [] data = bos.toByteArray();

        System.out.println(Arrays.toString(data));

        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        BufferedImage bImage2 = ImageIO.read(bis);
        ImageIO.write(bImage2, "jpg", new File("output.jpg") );
        System.out.println("image created");
    }
}
