package view;

import interface_adapter.holdings.HoldingsViewModel;
import interface_adapter.holdings.HoldingsState;
import interface_adapter.ViewManagerModel;
import interface_adapter.trade.TradeViewModel;
import interface_adapter.trade.TradeState;
import interface_adapter.view_transactions.ViewTransactionsController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HoldingsView extends JPanel {
    public final String viewName = "holdings";

    private HoldingsViewModel viewModel;
    private ViewManagerModel viewManagerModel;
    private TradeViewModel tradeViewModel;
    private ViewTransactionsController viewTransactionsController;

    public HoldingsView(HoldingsViewModel viewModel, ViewManagerModel viewManagerModel, TradeViewModel tradeViewModel, ViewTransactionsController viewTransactionsController) {
        this.viewModel = viewModel;
        this.viewManagerModel = viewManagerModel;
        this.tradeViewModel = tradeViewModel;
        this.viewTransactionsController = viewTransactionsController;
        initView();

        viewModel.addPropertyChangeListener(evt -> {
            if ("state".equals(evt.getPropertyName())) {
                initView();
            }
        });
    }

    private void initView() {
        // Clear panel
        removeAll();

        setLayout(new BorderLayout());
        try {
            // Title panel
            JPanel titlePanel = new JPanel(new BorderLayout(10, 10));
            // Create title
            JLabel title = new JLabel("<html> <b>" + viewModel.getState().getPortfolioName() + "</b>" + "<br/>" + "<html/>");
            JLabel defaultCurrency = new JLabel("<html> <font color='gray'>Portfolio Currency:  </font><b>" + viewModel.getState().getDefaultCurrency() + "</b>" + "<br/>" + "<html/>");
            title.setFont(new Font("Georgia", Font.PLAIN, 35));
            defaultCurrency.setFont(new Font("Georgia", Font.PLAIN, 20));
            titlePanel.add(title, BorderLayout.WEST);
            titlePanel.add(defaultCurrency, BorderLayout.EAST);
            add(titlePanel, BorderLayout.NORTH);

            // Create table model
            HoldingsTableModel tableModel = new HoldingsTableModel();
            tableModel.addColumn("<html><font color='gray'>Symbol</font><html>");
            tableModel.addColumn("<html><font color='gray'>Price(" + viewModel.getState().getDefaultCurrency() + ")</font><html>");
            tableModel.addColumn("<html><font color='gray'>Shares</font><html>");
            tableModel.addColumn("<html><font color='gray'>Total Value</font><html>");
            tableModel.addColumn("<html><font color='gray'>Change</font><html>");
            tableModel.addColumn("<html><font color='gray'>Change (%)</font><html>");

            DecimalFormat decimalFormat = new DecimalFormat("0.000");

            // Populate table
            List<String> symbols = viewModel.getState().getSymbols();
            List<Double> quotes = viewModel.getState().getQuotes();
            quotes.replaceAll(obj -> Double.parseDouble(decimalFormat.format(obj)));
            List<Double> shares = viewModel.getState().getShares();
            shares.replaceAll(obj -> Double.parseDouble(decimalFormat.format(obj)));
            List<Double> values = viewModel.getState().getValues();
            values.replaceAll(obj -> Double.parseDouble(decimalFormat.format(obj)));
            List<Double> changes = viewModel.getState().getChanges();
            List<Double> changePercents = viewModel.getState().getChangePercents();
            for (int i = 0; i < symbols.size(); i++) {
                // Format doubles to 3 decimal places with + or - sign in front and % sign at end of change percent
                String changesFormatted = String.format("%+.3f", changes.get(i));
                String changePercentsFormatted = String.format("%+.3f%%", changePercents.get(i));

                // set color of change to red if negative, green if positive, and black if 0
                if (changes.get(i) < 0) {
                    changesFormatted = "<html><font color='red'>" + changesFormatted + "</font></html>";
                    changePercentsFormatted = "<html><font color='red'>" + changePercentsFormatted + "</font></html>";
                } else if (changes.get(i) > 0) {
                    changesFormatted = "<html><font color='green'>" + changesFormatted + "</font></html>";
                    changePercentsFormatted = "<html><font color='green'>" + changePercentsFormatted + "</font></html>";
                }

                Object[] row = {symbols.get(i), quotes.get(i), shares.get(i), values.get(i), changesFormatted, changePercentsFormatted};
                tableModel.addRow(row);
            }

            // Create table
            JTable table = new JTable(tableModel);
            add(table);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            add(scrollPane, BorderLayout.CENTER);

            // Create buttons panel
            JPanel buttonsPanel = new JPanel(new BorderLayout(10, 10));

            // Create back button for going back to portfolio selection
            JButton backButton = new JButton("Back to Portfolio Selection");
            backButton.addActionListener(new BackButtonListener());
            backButton.setPreferredSize(new Dimension(350, 30));
            buttonsPanel.add(backButton, BorderLayout.WEST);

            // Create button for viewing transaction history
            JButton transactionHistoryButton = new JButton("View Transaction History");
            transactionHistoryButton.addActionListener(new TransactionHistoryButtonListener());
            transactionHistoryButton.setPreferredSize(new Dimension(350, 30));
            buttonsPanel.add(transactionHistoryButton, BorderLayout.CENTER);

            // Create button for adding trade
            JButton addTradeButton = new JButton("Add Trade");
            addTradeButton.addActionListener(new AddTradeButtonListener());
            addTradeButton.setPreferredSize(new Dimension(350, 30));
            buttonsPanel.add(addTradeButton, BorderLayout.EAST);

            add(buttonsPanel, BorderLayout.SOUTH);
        } catch (Exception exp) {
            JOptionPane.showMessageDialog(this, exp.getMessage());
        }
    }

    private class BackButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            viewManagerModel.setActiveView("portfolio_selection");
        }
    }

    private class AddTradeButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            tradeViewModel.getState().setPortfolioName(viewModel.getState().getPortfolioName());
            tradeViewModel.getState().setDefaultCurrency(viewModel.getState().getDefaultCurrency());
            viewManagerModel.setActiveView("trade");
        }
    }

    private class TransactionHistoryButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            viewTransactionsController.execute(viewModel.getState().getPortfolioName(), viewModel.getState().getDefaultCurrency());
            viewManagerModel.setActiveView("transactions");
        }
    }

}

