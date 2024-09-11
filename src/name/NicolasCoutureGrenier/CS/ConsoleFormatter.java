package name.NicolasCoutureGrenier.CS;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

public class ConsoleFormatter {
  private int consoleWidth = 80;
  final static String[] parentheseses = {"{}", "()", "[]"};
  final static Character[] potentialDelimiters = {' ', ',', ';'};
  final static String tab2 = "  ";

  public ConsoleFormatter(int consoleWidth) {
    super();
    this.consoleWidth = consoleWidth;
  }

  public ConsoleFormatter() {
    super();
  }

  public String format(String answer) {
    ArrayList<String> lines = new ArrayList<String>(List.of(answer.split("\n")));
    String tab = tab2;
    boolean isFirstIndentation = true;
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

      if (isFirstIndentation && tabcount > 2) {
        tabcount = tabcount / 2;
      }

      if (tabcount > 0) {
        isFirstIndentation = false;
      }
      String indentation = tab.repeat(tabcount);

      str = indentation + str.stripIndent().trim();

      if (str.length() <= consoleWidth) {
        lines.set(i, str.stripTrailing());
        continue;
      }

      boolean reeval = false;

      if (!reeval) {
        for (String d : parentheseses) {
          int first = str.indexOf(d.charAt(0));
          int last = str.lastIndexOf(d.charAt(1));

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
              for (int j = 0; j < parentheseses.length; j++) {
                if (innerBody.charAt(0) == parentheseses[j].charAt(0)) {
                  startsWithParenthesis = true;
                  break;
                }
              }
              int current = i + leadingLines.size();
              lines.add(current++, indentation + String.valueOf(d.charAt(0)));
              if (!startsWithParenthesis) {
                String[] splitted = {innerBody};
                Character lastChar =
                    innerBody.isBlank() ? null : innerBody.charAt(innerBody.length() - 1);
                Character ch = null;
                if (lastChar != null && lastChar.equals(';')) {
                  splitted = innerBody.split(";");
                  ch = ';';
                } else {
                  for (int j = 0; j < potentialDelimiters.length; j++) {
                    String[] candidate = innerBody.split(String.valueOf(potentialDelimiters[j]));
                    if (candidate.length > splitted.length) {
                      splitted = candidate;
                      ch = potentialDelimiters[j];
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

              lines.add(current++, indentation + String.valueOf(d.charAt(1)));
              String q = indentation + str.substring(last + 1);

              if (!q.isBlank()) lines.add(current++, q);

              if (lines.get(i).length() < consoleWidth - 2) {
                lines.set(i, lines.get(i) + " " + lines.get(i + 1));
                lines.remove(i + 1);
              }
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
                for (int j = 0; j < potentialDelimiters.length; j++) {
                  if (potentialDelimiters[j].equals(firstchar)) postblock = "";
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

  private List<String> splitOnBlank(String str) {
    List<String> lines = new ArrayList<String>();
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
