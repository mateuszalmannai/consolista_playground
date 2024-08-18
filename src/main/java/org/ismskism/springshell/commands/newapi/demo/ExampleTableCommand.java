package org.ismskism.springshell.commands.newapi.demo;

import org.ismskism.springshell.utils.LocalDateFormatter;
import org.ismskism.springshell.utils.ShellHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.table.*;

import java.time.LocalDate;
import java.util.Random;

@Command(group = "Table Examples")
public class ExampleTableCommand {
  public String[] CONTINENTS = {"Europe", "North America", "South America", "Africa", "Asia", "Austraila and Oceania"};
  public String[] COUNTRIES1 = {"Germany", "USA", "Brasil", "Nigeria", "China", "Australia"};
  public String[] COUNTRIES2 = {"France", "Canada", "Argentina", "Egypt", "India", "New Zeeland"};

  @Autowired
  private ShellHelper shellHelper;

  @Command(command = "sample-tables", description = "Display sample tables")
  public void sampleTables() {
    Object[][] sampleData = new String[][] {
        CONTINENTS,
        COUNTRIES1,
        COUNTRIES2
    };
    TableModel model = new ArrayTableModel(sampleData);
    TableBuilder tableBuilder = new TableBuilder(model);

    shellHelper.printInfo("air border style");
    tableBuilder.addFullBorder(BorderStyle.air);
    shellHelper.print(tableBuilder.build().render(80));

    shellHelper.printInfo("oldschool border style");
    tableBuilder.addFullBorder(BorderStyle.oldschool);
    shellHelper.print(tableBuilder.build().render(80));

    shellHelper.printInfo("fancy_light border style");
    tableBuilder.addFullBorder(BorderStyle.fancy_light);
    shellHelper.print(tableBuilder.build().render(80));

    shellHelper.printInfo("fancy_double border style");
    tableBuilder.addFullBorder(BorderStyle.fancy_double);
    shellHelper.print(tableBuilder.build().render(80));

    shellHelper.printInfo("mixed border style");
    tableBuilder.addInnerBorder(BorderStyle.fancy_light);
    tableBuilder.addHeaderBorder(BorderStyle.fancy_double);
    shellHelper.print(tableBuilder.build().render(80));
  }

  @Command(command = "table-formatter-demo", description = "Table formatter demo")
  public void tableFormatterDemo() {

    LocalDateFormatter dateFormatter = new LocalDateFormatter("dd.MM.YYYY");

    LocalDate now = LocalDate.now();
    Object[][] sampleData = new Object[][] {
        {"Date", "Value"},
        {"Today", now},
        {"Today minus 1", now.minusDays(1)},
        {"Today minus 2", now.minusDays(2)},
        {"Today minus 3", now.minusDays(3)}
    };

    TableModel model = new ArrayTableModel(sampleData);
    TableBuilder tableBuilder = new TableBuilder(model);
    tableBuilder.on(CellMatchers.ofType(LocalDate.class)).addFormatter(dateFormatter);
    tableBuilder.addFullBorder(BorderStyle.fancy_light);
    shellHelper.print(tableBuilder.build().render(30));
  }

  private static final String TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt " +
      "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco " +
      "laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in " +
      "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat " +
      "non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
  @Command(command = "table showcase", description = "Showcase table rendering")
  public Table table() {
    String[][] data = new String[3][3];
    TableModel model = new ArrayTableModel(data);
    TableBuilder tableBuilder = new TableBuilder(model);

    Random r = new Random();
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        data[i][j] = TEXT.substring(0, TEXT.length() / 2 + r.nextInt(TEXT.length() / 2));
        tableBuilder.on(at(i, j)).addAligner(SimpleHorizontalAligner.values()[j]);
        tableBuilder.on(at(i, j)).addAligner(SimpleVerticalAligner.values()[i]);
      }
    }

    return tableBuilder.addFullBorder(BorderStyle.fancy_light).build();
  }

  public static CellMatcher at(final int theRow, final int col) {
    return new CellMatcher() {
      @Override
      public boolean matches(int row, int column, TableModel model) {
        return row == theRow && column == col;
      }
    };
  }
}
