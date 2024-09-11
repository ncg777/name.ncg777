package name.NicolasCoutureGrenier.CS;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

/**
 * A class for formatting text for console output with automatic line wrapping.
 * 
 * The ConsoleFormatter class takes into account the console width and handles indentation and
 * special cases like parentheses, ensuring that the output remains readable and well-structured.
 * The formatting preserves leading and trailing whitespace based on specified rules and splits long
 * lines into multiple shorter lines as necessary.
 * 
 * This is not a code formatter; if you try to format ill-formatted code, it will produce 
 * ill-formatted code.
 */
public class ConsoleFormatter {
  private int consoleWidth = 80;
  private static String[] parentheseses = {"{}", "()", "[]"};
  private final static Character[] potentialDelimiters = {' ', ',', ';'};
  private final static String tab = "  ";

  /**
   * Constructs a ConsoleFormatter with the specified console width.
   * 
   * @param consoleWidth the width of the console for formatting
   * @throws IllegalArgumentException if console width is less than 1
   */
  public ConsoleFormatter(int consoleWidth) {
    super();
    this.consoleWidth = consoleWidth;
  }

  /**
   * Constructs a ConsoleFormatter with a default console width of 80.
   */
  public ConsoleFormatter() {
    super();
  }

  public void setConsoleWidth(int w) {
    if (w < 1) throw new IllegalArgumentException("you testing me?");
    this.consoleWidth = w;
  }

  public int getConsoleWidth() {
    return consoleWidth;
  }

  /**
   * Formats a given string by adjusting its structure to fit within the specified console width,
   * wrapping lines and preserving indentation.
   * 
   * @param answer the string to format
   * @return the formatted string, adjusted to fit within the console width
   */
  public String format(String answer) {
    ArrayList<String> lines = new ArrayList<>(List.of(answer.split("\n")));
    boolean isFirstIndentation = true;
    boolean is4SpaceTab = false;
    for (int i = 0; i < lines.size(); i++) {
      String str = lines.get(i);
      if (str.isBlank() || str.isEmpty()) continue;
      int tabcount = 0;

      while (str.charAt(tabcount) == '\t') {
        tabcount++;
        str = str.substring(1);
      }

      while (str.length() >= ((tabcount + 1) * tab.length())
          && str.substring(tabcount * tab.length(), (tabcount + 1) * tab.length()).equals(tab)) {
        tabcount++;
        str = str.substring(tab.length());
      }

      if (isFirstIndentation && tabcount > 2 && !str.startsWith("\t")) {
        is4SpaceTab = true;
      }

      if (is4SpaceTab) {
        tabcount = tabcount / 2;
      }

      if (tabcount > 0) {
        isFirstIndentation = false;
      }
      String indentation = tab.repeat(tabcount);

      str = indentation + str.trim();

      if (str.length() <= consoleWidth) {
        lines.set(i, str.stripTrailing());
        continue;
      }

      boolean reeval = false;

      if (!reeval) {
        for (String parenthesisChars : parentheseses) {
          int first = str.indexOf(parenthesisChars.charAt(0));
          int last = str.lastIndexOf(parenthesisChars.charAt(1));

          boolean matched = first > -1 && last > -1;
          if (matched) {
            String leading = str.substring(0, first).stripTrailing();
            String trailing = str.substring(last + 1).stripTrailing();
            if ((1 + last - first) > consoleWidth) {
              lines.remove(i);
              List<String> leadingLines = splitOnBlank(leading);
              lines.addAll(i, leadingLines);
              String innerBody = str.substring(first + 1, last).trim();
              boolean startsWithParenthesis = false;
              for (String p : parentheseses) {
                if (innerBody.charAt(0) == p.charAt(0)) {
                  startsWithParenthesis = true;
                  break;
                }
              }
              int current = i + leadingLines.size();
              lines.add(current++, indentation + String.valueOf(parenthesisChars.charAt(0)));
              if (lines.get(current - 2).length() < consoleWidth - 1) {
                lines.set(current - 2,
                    lines.get(current - 2) + " " + lines.get(current - 1).trim());
                current--;
                lines.remove(current);
              }
              if (!startsWithParenthesis) {
                String[] splitted = {innerBody};
                Character lastChar =
                    innerBody.isBlank() ? null : innerBody.charAt(innerBody.length() - 1);
                Character ch = null;
                if (lastChar != null && lastChar.equals(';')) {
                  splitted = innerBody.split(";");
                  ch = ';';
                } else {
                  for (Character delim : potentialDelimiters) {
                    String[] candidate = innerBody.split(String.valueOf(delim));
                    if (candidate.length > splitted.length) {
                      splitted = candidate;
                      ch = delim;
                    }
                  }
                }

                for (int j = 0; j < splitted.length; j++) {
                  String suffix = ch == null ? "" : ch.toString();
                  if (j == splitted.length - 1) {
                    if (suffix.equals(",") || suffix.equals(" ")) {
                      suffix = "";
                    }
                  }
                  lines.add(current++, indentation + tab + splitted[j] + suffix);
                }
              } else {
                lines.add(current++, indentation + tab + innerBody);
              }

              lines.add(current++, indentation + String.valueOf(parenthesisChars.charAt(1)));
              String q = indentation + str.substring(last + 1);

              if (!q.isBlank()) lines.add(current++, q);

              reeval = true;
            } else {
              lines.remove(i);
              String block = str.substring(first, last + 1);
              List<String> newLines = splitOnBlank(leading);
              String postblock = " ";
              Character firstchar = trailing.isBlank() ? null : trailing.charAt(0);
              if (firstchar == null)
                postblock = "";
              else {
                for (Character delim : potentialDelimiters) {
                  if (delim.equals(firstchar)) postblock = "";
                }
              }
              if (newLines.get(newLines.size() - 1).length() + 1 + block.length()
                  + postblock.length() + trailing.length() < consoleWidth) {
                newLines.set(newLines.size() - 1,
                    newLines.get(newLines.size() - 1) + " " + block + postblock + trailing);
              } else if (newLines.get(newLines.size() - 1).length() + 1
                  + block.length() < consoleWidth) {
                newLines.set(newLines.size() - 1, newLines.get(newLines.size() - 1) + " " + block);
                newLines.add(trailing);
              } else {
                if (block.length() + postblock.length() + trailing.length() < consoleWidth) {
                  newLines.add(indentation + block + postblock + trailing);
                } else {
                  newLines.add(indentation + block);
                  newLines.add(indentation + trailing);
                }
              }

              lines.addAll(i, newLines);

              reeval = true;
              break;
            }
          }

          if (reeval) break;
        }
      }

      if (!reeval) {
        List<String> newLines = splitOnBlank(str);
        if (newLines.size() > 1) {
          lines.remove(i);
          lines.addAll(i, newLines);
        }
      }

      if (reeval) i--;
    }

    return Joiner.on("\n").join(lines.stream().map((s) -> s.stripTrailing()).toList());
  }

  /**
   * Splits a string into multiple lines based on the console width, preserving indentation.
   * 
   * @param str the string to split
   * @return a list of strings representing the split lines
   */
  private List<String> splitOnBlank(String str) {
    List<String> lines = new ArrayList<>();
    lines.add(str);
    if (str.length() < consoleWidth) return lines;
    int i = 0;
    Character c;
    String indentation = str.substring(0, str.length() - str.stripIndent().length());
    str = str.substring(indentation.length());
    int search = Math.min(str.length() - 1, (consoleWidth - 1) - indentation.length());

    c = str.charAt(search);
    while (search > -1 && !String.valueOf(c).isBlank()) {
      search--;
      if (search >= 0) c = str.charAt(search);
    }

    if (search >= 0) {
      lines.remove(i);
      lines.add(i, indentation + str.substring(0, search + 1).stripTrailing());
      List<String> rest = splitOnBlank(str.substring(search + 1).stripTrailing());
      for (int j = 0; j < rest.size(); j++) {
        lines.add(i + j + 1, indentation + rest.get(j));
      }
    } else {
      int x = 0;
      String s = str;
      lines.remove(i);
      while (s.length() > 0) {
        boolean isLast = s.length() <= consoleWidth - indentation.length();
        lines.add(i + (x++), indentation
            + s.substring(0, Math.min(consoleWidth - indentation.length(), s.length())));
        if (!isLast) {
          s = s.substring(consoleWidth - indentation.length());
        } else {
          s = "";
        }
      }
    }
    return lines;
  }
}
