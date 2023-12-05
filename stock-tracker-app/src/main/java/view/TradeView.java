package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import interface_adapter.trade.TradeController;
import interface_adapter.trade.TradeState;
import interface_adapter.trade.TradeViewModel;
import interface_adapter.ViewManagerModel;

public class TradeView extends JPanel { //implements ActionListener, PropertyChangeListener {
    public final String viewName = "trade";
    private final TradeViewModel tradeViewModel;
    private final ViewManagerModel viewManagerModel;
    private final TradeController tradeController;

    private final JComboBox<String> tradeTypeComboBox;
    private final JTextField amountField;
    private final JTextField currencyField;
    private final JTextField sharesField;
    private final JTextField symbolField;
    private final JTextField priceField;
    private String selected = "";
    private JPanel panel;

    public TradeView(TradeViewModel tradeViewModel, ViewManagerModel viewManagerModel, TradeController tradeController) {
        this.viewManagerModel = viewManagerModel;
        this.tradeController = tradeController;
        this.tradeViewModel = tradeViewModel;
        this.tradeTypeComboBox = new JComboBox<>(new String[]{"Deposit", "Withdraw", "Buy", "Sell", "Exchange"});
        this.amountField = new JTextField(10);
        this.currencyField = new JTextField(5);
        this.sharesField = new JTextField(10);
        this.symbolField = new JTextField(5);
        this.priceField = new JTextField(10);

        initView();
    }

    private void initView() {
        //tradeViewModel.addPropertyChangeListener(this);

        JLabel title = new JLabel(tradeViewModel.TITLE_LABEL);
        title.setFont(new Font("Georgia", Font.PLAIN, 15));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // panel.add(title);

        panel = new JPanel(new GridLayout(0, 1));

        panel.add(new LabelTextPanel(new JLabel(tradeViewModel.TRADE_TYPE_LABEL), amountField));
        panel.add(new LabelTextPanel(new JLabel(tradeViewModel.AMOUNT_LABEL), amountField));
        panel.add(new LabelTextPanel(new JLabel(tradeViewModel.CURRENCY_LABEL), currencyField));
        panel.add(new LabelTextPanel(new JLabel(tradeViewModel.SHARES_LABEL), sharesField));
        panel.add(new LabelTextPanel(new JLabel(tradeViewModel.SYMBOL_LABEL), symbolField));
        panel.add(new LabelTextPanel(new JLabel(tradeViewModel.PRICE_LABEL), priceField));

        JPanel buttons = new JPanel();

        JButton cancelButton = new JButton(tradeViewModel.CANCEL_BUTTON_LABEL);
        cancelButton.addActionListener(new CancelButtonListener());
        buttons.add(cancelButton);

        JButton confirmButton = new JButton(tradeViewModel.CONFIRM_BUTTON_LABEL);
        confirmButton.addActionListener(new ConfirmButtonListener());
        buttons.add(confirmButton);

        showRelevantFields();

        tradeTypeComboBox.addItemListener(new TradeTypeComboBoxListener());

        panel.add(buttons);
        panel.add(tradeTypeComboBox);

        add(panel);

/*
        trade.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if (evt.getSource().equals("trade")) {
                            TradeState currentState = tradeViewModel.getState();

                            tradeController.execute(currentState.getPortfolioName(), currentState.getTradeValue(),
                                    currentState.getAssetInSymbol(), currentState.getAssetOutSymbol(),
                                    currentState.getAmountIn(), currentState.getAmountOut());
                            //TODO: Need to fix actionPerformed method, in particular the execute statement.
                        }
                    }
                }
        );
        cancel.addActionListener(this);

        // Why are we using key listeners, we don't need to listen for every key press?
        valueInputField.addKeyListener(
                new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        TradeState currentState = tradeViewModel.getState();
                        currentState.setTradeValue(Double.parseDouble(valueInputField.getText() + e.getKeyChar()));
                        tradeViewModel.setState(currentState);
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {}

                    @Override
                    public void keyReleased(KeyEvent e) {}
                }
        );

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(title);
        this.add(amountInfo);
        this.add(buttons);
*/
    }

    private class ConfirmButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // gets trade type
            String tradeType = (String) tradeTypeComboBox.getSelectedItem();

            // TODO: trading fee is set to 0.0 right now

            String portfolio = tradeViewModel.getState().getPortfolioName();
            String defaultCurrency = tradeViewModel.getState().getDefaultCurrency();

            assert tradeType != null;
            switch (tradeType) {
                case "Deposit" -> {
                    double amount = Double.parseDouble(amountField.getText());
                    String currency = currencyField.getText();
                    try {
                        tradeController.execute(portfolio, "$" + currency, "", amount, 0.0, 0.0);
                    } catch (RuntimeException exp) {
                        if (exp.getMessage().equals("Cannot invoke \"com.fasterxml.jackson.databind.JsonNode.fields()\" because \"timeSeries\" is null")) {
                            JOptionPane.showMessageDialog(panel, "API Key error. Please check again.");
                        }
                        JOptionPane.showMessageDialog(panel, exp.getMessage());
                    }
                }
                case "Withdraw" -> {
                    double amount = Double.parseDouble(amountField.getText());
                    String currency = currencyField.getText();
                    try {
                        tradeController.execute(portfolio, "", "$" + currency, 0.0, amount, 0.0);
                    } catch (RuntimeException exp) {
                        if (exp.getMessage().equals("Cannot invoke \"com.fasterxml.jackson.databind.JsonNode.fields()\" because \"timeSeries\" is null")) {
                            JOptionPane.showMessageDialog(panel, "API Key error. Please check again.");
                        }
                        JOptionPane.showMessageDialog(panel, exp.getMessage());
                    }
                }
                case "Buy" -> {
                    double shares = Double.parseDouble(sharesField.getText());
                    String symbol = symbolField.getText();
                    double price = Double.parseDouble(priceField.getText());
                    tradeController.execute(portfolio, symbol, defaultCurrency, shares, shares * price, 0.0);
                }
                case "Sell" -> {
                    double shares = Double.parseDouble(sharesField.getText());
                    String symbol = symbolField.getText();
                    double price = Double.parseDouble(priceField.getText());
                    tradeController.execute(portfolio, defaultCurrency, symbol, shares * price, shares, 0.0);
                }
                case "Exchange" -> {
                    // TODO
                }
            }
        }
    }

    private class CancelButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            viewManagerModel.setActiveView("holdings");
        }

    }

    private class TradeTypeComboBoxListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                showRelevantFields();
            }
        }
    }

    /* private class TradeTypeComboBoxListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            showRelevantFields();
        }
    } */

    private void showRelevantFields() {
        /* if ("Deposit".equals(tradeTypeComboBox.getSelectedItem())) {
            amountField.setVisible(true);
            currencyField.setVisible(true);
            sharesField.setVisible(false);
            symbolField.setVisible(false);
            priceField.setVisible(false);
        } else if ("Withdraw".equals(tradeTypeComboBox.getSelectedItem())) {
            amountField.setVisible(true);
            currencyField.setVisible(true);
            sharesField.setVisible(false);
            symbolField.setVisible(false);
            priceField.setVisible(false);
        } else if ("Buy".equals(tradeTypeComboBox.getSelectedItem())) {
            amountField.setVisible(true);
            currencyField.setVisible(true);
            sharesField.setVisible(true);
            symbolField.setVisible(false);
            priceField.setVisible(false);
        } else if ("Sell".equals(tradeTypeComboBox.getSelectedItem())) {
            amountField.setVisible(true);
            currencyField.setVisible(true);
            sharesField.setVisible(true);
            symbolField.setVisible(false);
            priceField.setVisible(false);
        } else {
            amountField.setVisible(true);
            currencyField.setVisible(true);
            sharesField.setVisible(true);
            symbolField.setVisible(true);
            priceField.setVisible(true);
        }
        tradeTypeComboBox.addItemListener(new TradeTypeComboBoxListener()); */

        String tradeType = (String) tradeTypeComboBox.getSelectedItem();

        // Hide all fields
        amountField.setVisible(false);
        currencyField.setVisible(false);
        sharesField.setVisible(false);
        symbolField.setVisible(false);
        priceField.setVisible(false);

        // Show relevant
//        if (!selected.equals(tradeType)) {
//
//        }
        switch (tradeType) {
            case "Deposit":
            case "Withdraw":
                amountField.setVisible(true);
                currencyField.setVisible(true);
                break;
            case "Buy":
            case "Sell":
                sharesField.setVisible(true);
                symbolField.setVisible(true);
                priceField.setVisible(true);
                break;
            case "Exchange":
                amountField.setVisible(true);
                currencyField.setVisible(false);
                sharesField.setVisible(true);
                symbolField.setVisible(true);
                priceField.setVisible(false);
                break;
            default:
                // TODO: implement default case here.
                break;
        }
        panel.revalidate();
        panel.repaint();
        // tradeTypeComboBox.addItemListener(new TradeTypeComboBoxListener());
    }
/*
@Override
public void actionPerformed(ActionEvent e) {

} */

/* @Override
public void propertyChange(PropertyChangeEvent evt) {
    TradeState state = (TradeState) evt.getNewValue();
    } */
}
