package gui;

import application.Main;
import db.DbIntegrityExeption;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SellerListController implements Initializable, DataChangeListener {

    @FXML
    private SellerService service;

    @FXML
    private TableView<Seller> SellerTableView;

    @FXML
    private TableColumn<Seller, Integer> tableColumnId;

    @FXML
    private TableColumn<Seller, String> tableColumnName;

    @FXML
    private TableColumn<Seller,Seller> tableColumnEDIT;

    @FXML
    private TableColumn<Seller, Seller> tableColumnREMOVE;

    @FXML
    private Button btNew;

    @FXML
    private ObservableList<Seller> observableList;

    @FXML
    public void onBtNewAction(ActionEvent event){
        Stage parentStage = Utils.currentStage(event);
        Seller obj = new Seller();

        createDialogForm(obj, "/gui/SellerForm.fxml", parentStage );

    }

    public void setService(SellerService service) {
        this.service = service;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeNodes();


    }

    private void initializeNodes() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

        Stage stage = (Stage) Main.getMainScene().getWindow();
        SellerTableView.prefHeightProperty().bind(stage.heightProperty());

    }

    public void updateTabeView(){
        if(service == null){
            throw new IllegalStateException("Service was null");
        }
        List<Seller> list = service.findAll();

        observableList = FXCollections.observableArrayList(list);
        SellerTableView.setItems(observableList);
        initEditButtons();
        initRemoveButtons();

    }


    private void createDialogForm(Seller obj, String path, Stage parentStage){
       /* try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Pane pane = loader.load();

            SellerFormController controller = loader.getController();
            controller.setSeller(obj);
            controller.setSellerService(new SellerService());
            controller.subscribeDtaChangeListner(this);

            controller.updateFormData();

            Stage diaogStage = new Stage();
            diaogStage.setTitle("Enter Seller Data");
            diaogStage.setScene(new Scene(pane));
            diaogStage.setResizable(false);
            diaogStage.initOwner(parentStage);
            diaogStage.initModality(Modality.WINDOW_MODAL);
            diaogStage.showAndWait();

        }catch (IOException e){
            Alerts.showAlert("IO Exception", "Error loading View", e.getMessage(), Alert.AlertType.ERROR);
        }*/

    }

    @Override
    public void onDataChanged() {
        updateTabeView();
    }


    private void initEditButtons() {
        tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
            private final Button button = new Button("edit");
            @Override
            protected void updateItem(Seller obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(
                        event -> createDialogForm(
                                obj, "/gui/SellerForm.fxml",Utils.currentStage(event)));
            }
        });
    }

    private void initRemoveButtons() {
        tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
            private final Button button = new Button("remove");
            @Override
            protected void updateItem(Seller obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event -> removeEntity(obj));
            }
        });
    }

    private void removeEntity(Seller obj) {
        Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure you want to delete?") ;
        if(result.get() == ButtonType.OK){
            if(service == null){
                throw new IllegalStateException(("Serviec was null"));
            }
            try {
                service.remove(obj);
                updateTabeView();
            }catch (DbIntegrityExeption e){
                Alerts.showAlert("Error removing Object", null, e.getMessage(), Alert.AlertType.ERROR);
            }

        }
    }


}
