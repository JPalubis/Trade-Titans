package interface_adapter.portfolio_view;

import interface_adapter.ViewManagerModel;
import use_case.portfolio.portfolioOutputBoundary;
import use_case.portfolio.portfolioOutputData;

public class PortfolioPresenter implements portfolioOutputBoundary {

    private PortfolioViewModel portfolioViewModel;
    private ViewManagerModel viewManagerModel;

    public PortfolioPresenter(ViewManagerModel viewManagerModel,
                              PortfolioViewModel portfolioViewModel) {
        this.portfolioViewModel = portfolioViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(portfolioOutputData portfolioName) {
        PortfolioViewState portfolioViewState = portfolioViewModel.getState();
        this.portfolioViewModel.setState(portfolioViewModel.getState());
        viewManagerModel.setActiveView(portfolioViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        PortfolioViewState portfolioViewState = portfolioViewModel.getState();
        portfolioViewState.getFailportfolioView();
        portfolioViewModel.firePropertyChanged();
    }
}