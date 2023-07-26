package model;

import java.awt.*;
import java.util.ArrayList;

public class Colors {

    private static final ArrayList<Color> colorsList = new ArrayList<>(){{
        add(Color.BLUE);
        add(Color.GREEN);
        add(Color.RED);
        add(new Color(117,168,50));
//        add(Color.);
//        add(Color.);
//        add(Color.);
//        add(Color.);
//        add(Color.);
//        add(Color.);
//        add(Color.);
//        add(Color.);
    }};

    private static int i = (int) (Math.random() * 10);

    public static Color getColor() {
        i++;
        i %= colorsList.size();
        return colorsList.get(i);
    }


    public static Color blend(Color color1, Color color2) {
        return new Color((color1.getRed() + color2.getRed()) / 2, (color1.getGreen() + color2.getGreen()) / 2,(color1.getBlue() + color2.getBlue()) / 2);
    }

    public static Color lighter(Color color) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), 127);
    }

    public static Color darker(Color color) {
        return new Color(color.getRed() / 2, color.getGreen() / 2, color.getBlue() / 2);
    }
}