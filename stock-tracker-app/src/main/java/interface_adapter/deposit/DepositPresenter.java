package interface_adapter.deposit;

import interface_adapter.ViewManagerModel;
import use_case.deposit.DepositOutputBoundary;
import use_case.deposit.DepositOutputData;

public class DepositPresenter implements DepositOutputBoundary {
    private final DepositViewModel depositViewModel;
    private ViewManagerModel viewManagerModel;

    public DepositPresenter(DepositViewModel depositViewModel,
                            ViewManagerModel viewManagerModel){
        this.depositViewModel = depositViewModel;
        this.viewManagerModel = viewManagerModel;
    }


    @Override
    public void prepareSuccessView(DepositOutputData user) {
        DepositState depositState = depositViewModel.getState();
        // TODO: fix
        this.depositViewModel.setState(depositState);
        viewManagerModel.setActiveView(depositViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        DepositState depositState = depositViewModel.getState();
        depositState.getNegativeDepositError();
        depositViewModel.firePropertyChanged();
    }
}