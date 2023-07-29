package model;

import java.awt.*;
import java.util.ArrayList;

public class Colors {

    private static final ArrayList<Color> colorsList = new ArrayList<>(){{
        add(Color.BLUE);
        add(Color.RED);
        add(Color.GREEN);
        add(new Color(117,168,50));
        add(new Color(120,30,130));
        add(new Color(170,110,240));
        add(new Color(245,80,230));
        add(new Color(245,80,50));
        add(new Color(210,170,50));
        add(new Color(240,220,170));
        add(new Color(90,210,245));
        add(new Color(60,210,180));
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