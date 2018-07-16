package net.wizardsoflua.annotation.processor;

public class StringUtils {
  /**
   * <p>
   * Capitalizes a String changing the first letter to title case as per
   * {@link Character#toTitleCase(char)}. No other letters are changed.
   * </p>
   *
   * <p>
   * For a word based algorithm, see
   * {@link org.apache.commons.lang3.text.WordUtils#capitalize(String)}. A {@code null} input String
   * returns {@code null}.
   * </p>
   *
   * <pre>
   * StringUtils.capitalize(null)  = null
   * StringUtils.capitalize("")    = ""
   * StringUtils.capitalize("cat") = "Cat"
   * StringUtils.capitalize("cAt") = "CAt"
   * </pre>
   *
   * @param str the String to capitalize, may be null
   * @return the capitalized String, {@code null} if null String input
   * @see org.apache.commons.lang3.text.WordUtils#capitalize(String)
   * @see #uncapitalize(String)
   * @since 2.0
   */
  public static String capitalize(final String str) {
    int strLen;
    if (str == null || (strLen = str.length()) == 0) {
      return str;
    }

    char firstChar = str.charAt(0);
    if (Character.isTitleCase(firstChar)) {
      // already capitalized
      return str;
    }

    return new StringBuilder(strLen).append(Character.toTitleCase(firstChar))
        .append(str.substring(1)).toString();
  }
}
