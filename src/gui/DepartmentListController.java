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
import model.entities.Department;
import model.services.DepartmentService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class DepartmentListController implements Initializable, DataChangeListener {

    @FXML
    private DepartmentService service;

    @FXML
    private TableView<Department> departmentTableView;

    @FXML
    private TableColumn<Department, Integer> tableColumnId;

    @FXML
    private TableColumn<Department, String> tableColumnName;

    @FXML
    private TableColumn<Department,Department> tableColumnEDIT;

    @FXML
    private TableColumn<Department, Department> tableColumnREMOVE;

    @FXML
    private Button btNew;

    @FXML
    private ObservableList<Department> observableList;

    @FXML
    public void onBtNewAction(ActionEvent event){
        Stage parentStage = Utils.currentStage(event);
        Department obj = new Department();

        createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage );

    }

    public void setService(DepartmentService service) {
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
        departmentTableView.prefHeightProperty().bind(stage.heightProperty());

    }

    public void updateTabeView(){
        if(service == null){
            throw new IllegalStateException("Service was null");
        }
        List<Department> list = service.findAll();

        observableList = FXCollections.observableArrayList(list);
        departmentTableView.setItems(observableList);
        initEditButtons();
        initRemoveButtons();

    }

    private void createDialogForm(Department obj, String path, Stage parentStage){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Pane pane = loader.load();

            DepartmentFormController controller = loader.getController();
            controller.setDepartment(obj);
            controller.setDepartmentService(new DepartmentService());
            controller.subscribeDtaChangeListner(this);

            controller.updateFormData();

            Stage diaogStage = new Stage();
            diaogStage.setTitle("Enter Department Data");
            diaogStage.setScene(new Scene(pane));
            diaogStage.setResizable(false);
            diaogStage.initOwner(parentStage);
            diaogStage.initModality(Modality.WINDOW_MODAL);
            diaogStage.showAndWait();

        }catch (IOException e){
            e.printStackTrace();
            Alerts.showAlert("IO Exception", "Error loading View", e.getMessage(), Alert.AlertType.ERROR);
        }

    }

    @Override
    public void onDataChanged() {
        updateTabeView();
    }


    private void initEditButtons() {
        tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
            private final Button button = new Button("edit");
            @Override
            protected void updateItem(Department obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(
                        event -> createDialogForm(
                                obj, "/gui/DepartmentForm.fxml",Utils.currentStage(event)));
            }
        });
    }

    private void initRemoveButtons() {
        tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>() {
            private final Button button = new Button("remove");
            @Override
            protected void updateItem(Department obj, boolean empty) {
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

    private void removeEntity(Department obj) {
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
