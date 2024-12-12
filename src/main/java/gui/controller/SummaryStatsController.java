package gui.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

public class SummaryStatsController {
    @FXML
    public Label income;
    @FXML
    public Label expenses;

    @FXML
    public LineChart balanceToYearLineChart;
    @FXML
    public LineChart incomeToYearLineChart;
    @FXML
    public LineChart expensesToYearLineChart;
    @FXML
    public BarChart expensesToMonthBarChart;

    @FXML
    public PieChart withdrawsPieGraph;
    @FXML
    public PieChart depositsPieGraph;
    @FXML
    public PieChart topExpensesPieGraph;

    @FXML
    public void goBack(){

    }
}
