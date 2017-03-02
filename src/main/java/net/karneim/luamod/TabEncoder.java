package net.karneim.luamod;

public class TabEncoder {
  public static String encode(String text) {
    int TABSIZE = 4;
    StringBuilder result = new StringBuilder();
    char[] chars = text.toCharArray();
    int length = 0;
    for (int i=0; i<chars.length; ++i) {
      char c = chars[i];
      if ( c == '\n') {
        result.append(c);
        length = 0;
      } else if ( c == '\t') {
        int pos = (length / TABSIZE) * TABSIZE + TABSIZE;
        int dif = pos - length;
        for (int k=0; k<dif; ++k) {
          result.append(' ');
          length++;
        }
      } else {
        result.append(c);
        length++;
      }
    }
    return result.toString();
  }
}
