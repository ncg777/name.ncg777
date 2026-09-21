package name.ncg777.maths.neural.apps;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import name.ncg777.maths.neural.market.*;

/** Searchable exchange directories and an explicitly partial, sequential daily screener. */
public final class MarketScreenerDialog extends JDialog {
  private final JTextField search = new JTextField(22);
  private final JComboBox<String> market = new JComboBox<>(new String[]{"All markets", "U.S.", "Canada"});
  private final JComboBox<String> horizon = new JComboBox<>(new String[]{"Next trading day", "Next 2 trading days"});
  private final JSpinner epochs = new JSpinner(new SpinnerNumberModel(5, 1, 40, 1));
  private final JLabel status = new JLabel("Load the directories to start. Scans may take hours across all listings.");
  private final JTextArea reports = new JTextArea(4, 80);
  private final DefaultTableModel catalog = model(new String[]{"Quote symbol", "Company / instrument", "Market", "Exchange", "Status"});
  private final DefaultTableModel results = model(new String[]{"Quote symbol", "Company", "Price date", "P(gain 1d) %", "P(gain 2d) %", "Test error", "Baseline error", "Test windows", "Model / failure"});
  private final JTable listings = new JTable(catalog), ranking = new JTable(results);
  private final TableRowSorter<DefaultTableModel> filter = new TableRowSorter<>(catalog), rank = new TableRowSorter<>(results);
  private final JButton reload = new JButton("Load / refresh symbols"), selected = new JButton("Scan selected"), all = new JButton("Scan filtered"), pause = new JButton("Pause"), resume = new JButton("Resume"), chart = new JButton("Open chart");
  private SwingWorker<?, ?> worker;
  private List<SymbolCatalog.Symbol> symbols = List.of(), queue = List.of();
  private volatile int cursor, failures;
  private int passes;
  private volatile boolean paused;
  private final Consumer<String> openChart;
  public MarketScreenerDialog(JFrame owner, Consumer<String> openChart) {
    super(owner, "U.S. and Canadian symbol screener", false); this.openChart = openChart;
    setSize(1220, 760); setLocationRelativeTo(owner); setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    JPanel root = new JPanel(new BorderLayout(8, 8)); root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    JPanel top = new JPanel(new GridLayout(0,1));
    JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
    actions.add(reload); actions.add(new JLabel("Search")); actions.add(search); actions.add(market); actions.add(chart);
    top.add(actions); top.add(new JLabel(SymbolCatalog.COVERAGE));
    JPanel scan = new JPanel(new FlowLayout(FlowLayout.LEFT));
    scan.add(selected); scan.add(all); scan.add(pause); scan.add(resume); scan.add(horizon); scan.add(new JLabel("Training passes")); scan.add(epochs); top.add(scan);
    top.add(new JLabel("Estimated gain before costs; uncalibrated across symbols. Lower test error is better. Rankings cover successful scans only."));
    root.add(top, BorderLayout.NORTH);
    listings.setRowSorter(filter); ranking.setRowSorter(rank); listings.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    listings.getColumnModel().getColumn(0).setPreferredWidth(130); listings.getColumnModel().getColumn(1).setPreferredWidth(600); ranking.getColumnModel().getColumn(1).setPreferredWidth(230);
    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(listings), new JScrollPane(ranking)); split.setResizeWeight(.45); root.add(split);
    reports.setEditable(false); reports.setLineWrap(true); reports.setWrapStyleWord(true);
    JPanel bottom = new JPanel(new BorderLayout()); bottom.add(status, BorderLayout.NORTH); bottom.add(new JScrollPane(reports)); root.add(bottom, BorderLayout.SOUTH); setContentPane(root);
    search.getDocument().addDocumentListener(new DocumentListener(){ public void insertUpdate(DocumentEvent e){filter();} public void removeUpdate(DocumentEvent e){filter();} public void changedUpdate(DocumentEvent e){filter();} });
    market.addActionListener(e -> filter()); horizon.addActionListener(e -> sort());
    reload.addActionListener(e -> load()); selected.addActionListener(e -> begin(true)); all.addActionListener(e -> begin(false));
    pause.addActionListener(e -> { paused = true; status.setText("Pausing after the current symbol (up to 25 seconds for a request)…"); pause.setEnabled(false); });
    resume.addActionListener(e -> scan());
    chart.addActionListener(e -> {
      JTable table = ranking.getSelectedRow() >= 0 ? ranking : listings;
      if (table.getSelectedRow() >= 0) openChart.accept(table.getValueAt(table.getSelectedRow(), 0).toString());
    });
    addWindowListener(new WindowAdapter(){ @Override public void windowClosed(WindowEvent e){ if(worker != null) worker.cancel(true); } });
    sort(); busy(false);
  }
  private static DefaultTableModel model(String[] columns) {
    return new DefaultTableModel(columns,0) {
      @Override public boolean isCellEditable(int r,int c){return false;}
      @Override public Class<?> getColumnClass(int c){return columns.length == 9 && c >= 3 && c <= 7 ? (c == 7 ? Integer.class : Double.class) : String.class;}
    };
  }
  private void filter() {
    String query = search.getText().strip().toLowerCase(Locale.ROOT), region = (String)market.getSelectedItem();
    filter.setRowFilter(new RowFilter<>() { public boolean include(Entry<? extends DefaultTableModel,? extends Integer> e) {
      return (region.equals("All markets") || region.equals(e.getStringValue(2)))
          && (e.getStringValue(0) + " " + e.getStringValue(1) + " " + e.getStringValue(3)).toLowerCase(Locale.ROOT).contains(query);
    }});
    listings.setToolTipText(listings.getRowCount() + " matching listings of " + symbols.size());
  }
  private void sort(){ rank.setSortKeys(List.of(new RowSorter.SortKey(horizon.getSelectedIndex()==0 ? 3 : 4, SortOrder.DESCENDING))); }
  private void busy(boolean value) {
    reload.setEnabled(!value); selected.setEnabled(!value && !symbols.isEmpty()); all.setEnabled(!value && !symbols.isEmpty());
    epochs.setEnabled(!value && cursor >= queue.size()); resume.setEnabled(!value && cursor < queue.size()); pause.setEnabled(value && !queue.isEmpty());
  }
  private void load() {
    if(worker != null) return; busy(true); pause.setEnabled(false); status.setText("Loading five official exchange directories…");
    worker = new SwingWorker<SymbolCatalog.Catalog,Void>() {
      protected SymbolCatalog.Catalog doInBackground() throws Exception { return SymbolCatalog.load(); }
      protected void done() {
        try { var data = get(); symbols = data.symbols(); catalog.setRowCount(0);
          for(var s : symbols) catalog.addRow(new Object[]{s.ticker(),s.name(),s.market(),s.exchange(),s.status()});
          reports.setText("Fetched " + data.fetchedAt() + "\n" + String.join("\n",data.reports()));
          status.setText(symbols.size()+" listings loaded. Search, select rows, or scan every filtered listing."); filter();
        } catch(Exception e){ status.setText("Directory load failed: " + message(e)); }
        finally {worker=null; busy(false);}
      }
    }; worker.execute();
  }
  private void begin(boolean onlySelected) {
    if(worker != null) return;
    int[] rows = onlySelected ? listings.getSelectedRows() : java.util.stream.IntStream.range(0,listings.getRowCount()).toArray();
    if(rows.length == 0) {status.setText("No listings selected / matching."); return;}
    queue = Arrays.stream(rows).mapToObj(i -> symbols.get(listings.convertRowIndexToModel(i))).toList();
    cursor=0; failures=0; passes=(Integer)epochs.getValue(); results.setRowCount(0); scan();
  }
  private void scan() {
    if(worker != null || cursor >= queue.size()) return; paused=false; busy(true);
    worker = new SwingWorker<Void,Object[]>() {
      protected Void doInBackground() throws Exception {
        while(cursor < queue.size() && !isCancelled() && !paused) {
          var s=queue.get(cursor); Object[] row; boolean blocked=false;
          try {
            if(!s.status().equalsIgnoreCase("Active")) throw new IllegalArgumentException("Listing is " + s.status());
            var data=MarketData.yahoo(s.ticker(),MarketData.Interval.DAY); MarketScreener.requireFresh(data,Instant.now());
            var fit=MarketForecaster.fit(data,800,passes); var score=MarketScreener.score(fit);
            var date=data.completed().get(data.completed().size()-1).time().atZone(ZoneOffset.UTC).toLocalDate().toString();
            row=new Object[]{s.ticker(),s.name(),date,100*score.oneDay(),100*score.twoDays(),score.testBrier(),score.baselineBrier(),score.testWindows(),fit.useNetwork()?"Network":"Independent baseline"};
          } catch(InterruptedException | CancellationException e) {throw e;}
          catch(Exception e) { failures++; String error=message(e); blocked=error.contains("HTTP 429") || error.contains("HTTP 403"); row=new Object[]{s.ticker(),s.name(),"",null,null,null,null,null,error}; }
          cursor++; publish(row);
          if(blocked){paused=true; break;}
          if(cursor < queue.size() && !paused) Thread.sleep(1500);
        }
        return null;
      }
      protected void process(List<Object[]> rows){ for(var row:rows) results.addRow(row); progress(); }
      protected void done(){
        try{get();} catch(Exception e){if(!isCancelled()) reports.append("\nScan stopped: "+message(e));}
        finally {worker=null; busy(false); progress();}
      }
    }; worker.execute();
  }
  private void progress(){status.setText((worker==null ? (cursor<queue.size()?"Paused — ":"Finished — ") : "Scanning — ")+cursor+" / "+queue.size()+" attempted; "+(cursor-failures)+" ranked; "+failures+" failed. Rankings are limited to this scan.");}
  private static String message(Throwable e){while(e.getCause()!=null)e=e.getCause(); return e.getMessage()==null?e.toString():e.getMessage();}
}
