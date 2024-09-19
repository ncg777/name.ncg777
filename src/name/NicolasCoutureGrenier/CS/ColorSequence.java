package name.NicolasCoutureGrenier.CS;

import java.awt.Color;
import java.util.Map;
import java.util.TreeMap;

import name.NicolasCoutureGrenier.Maths.Enumerations.MixedRadixEnumeration;

public class ColorSequence {
  final static public int MAX_RGB = 255;
  private Color[] colors;

  private int delta_level = -1;
  private int nbsubdiv = -1;

  public ColorSequence(int minNbColors) {
    double cuberoot = Math.pow(minNbColors, 1.0 / 3.0);
    nbsubdiv = (int) Math.ceil(cuberoot);
    int nbboubdaries = nbsubdiv + 1;
    int total = nbboubdaries * nbboubdaries * nbboubdaries;
    delta_level = MAX_RGB / nbsubdiv;


    Map<Integer, Integer[]> colors_in_order = new TreeMap<Integer, Integer[]>();
    int[] level = new int[total];
    int nblevels = 2 * 2 * 2;
    int[] levels_count = new int[nblevels];
    for (int i = 0; i < nblevels; i++) {
      levels_count[i] = 0;
    }

    {
      Integer[] base = {nbboubdaries, nbboubdaries, nbboubdaries};
      MixedRadixEnumeration mre = new MixedRadixEnumeration(base);
      int k = 0;
      while (mre.hasMoreElements()) {
        Integer[] triple = mre.nextElement();
        colors_in_order.put(k, triple);
        int max = -1;
        for (int j = 0; j < 3; j++) {
          if (triple[j] > max) {
            max = triple[j];
          }
        }
        int lv = 0;
        for (int j = 0; j < 3; j++) {
          if (triple[j] == max) {
            lv += (int) Math.pow(2.0, j);
          }
        }
        /**
         * Level 0 will be empty, but it is simple to leave it like that.
         */
        level[k] = lv;
        levels_count[lv]++;
        k++;

      }
    }

    colors = new Color[total];

    int current = 0;
    int current_level = 0;
    int i = 0;
    while (true) {
      if (current == total) {
        break;
      }
      while (levels_count[current_level] == 0) {
        current_level = (current_level + 1) % nblevels;
      }
      if (level[i] == current_level) {
        levels_count[current_level]--;
        Integer[] c = colors_in_order.get(i);
        int r = mapLevel(c[0]);
        int g = mapLevel(c[1]);
        int b = mapLevel(c[2]);
        colors[current++] = new Color(r, g, b);
        current_level = (current_level + 1) % nblevels;
      }
      i = (i + 1) % total;
    }

  }

  public Color get(int i) {
    return colors[i];
  }

  private int mapLevel(int x) {
    return (nbsubdiv - x) * delta_level;
  }

  public int size() {
    return colors.length;
  }

}

