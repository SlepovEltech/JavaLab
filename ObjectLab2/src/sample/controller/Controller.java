package sample.controller;

import java.io.FileReader;
import java.net.URL;
import java.util.ResourceBundle;

import java.io.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;

import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;

import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.report.HtmlReport;
import sample.report.PdfReport;
import sample.entity.Doctor;
import sample.entity.Patient;
import sample.exception.EmptyPersonException;
import sample.exception.NoFileException;
import sample.parser.XmlParser;
import sample.parser.XmlSaver;
import sample.thread.MyThread;

public class Controller {

    private ObservableList<Doctor> doctorData = FXCollections.observableArrayList();
    private ObservableList<Patient> patientData = FXCollections.observableArrayList();
    private File doctorSrc, patientSrc;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<Doctor> doctorList;

    @FXML
    private TableColumn<Doctor, Integer> doctorId;

    @FXML
    private TableColumn<Doctor, String> docSurname;

    @FXML
    private TableColumn<Doctor, String> docName;

    @FXML
    private TableColumn<Doctor, String> docMiddleName;

    @FXML
    private TableColumn<Doctor, String> docSpecialty;

    @FXML
    private TableColumn<Doctor, String> docNote;

    @FXML
    private TextField addDocSurname;

    @FXML
    private TextField addDocName;

    @FXML
    private TextField addDocMiddle;

    @FXML
    private TextField addDocSpecialty;

    @FXML
    private TextField addDocNote;

    @FXML
    private Button addDoctor;

    @FXML
    private Button loadDoctor;

    @FXML
    private Button deleteDoctor;

    @FXML
    private Button saveDoctor;

    @FXML
    private TableView<Patient> patientList;

    @FXML
    private TableColumn<Patient, Integer> patientId;

    @FXML
    private TableColumn<Patient, String> patientSurname;

    @FXML
    private TableColumn<Patient, String> patientName;

    @FXML
    private TableColumn<Patient, String> patientMiddleName;

    @FXML
    private TableColumn<Patient, String> patientDiagnos;

    @FXML
    private TableColumn<Patient, String> patientNote;

    @FXML
    private TextField addPatSurname;

    @FXML
    private TextField addPatName;

    @FXML
    private TextField addPatMiddle;

    @FXML
    private TextField addPatDiagnos;

    @FXML
    private TextField addPatNote;

    @FXML
    private Button addPatient;

    @FXML
    private Button loadPatient;

    @FXML
    private Button deletePatient;

    @FXML
    private Button savePatient;

    @FXML
    private Button makePdfDoctor;

    @FXML
    private Button makePdfPatient;

    @FXML
    private Button makeHtmlDoctor;

    @FXML
    private Button makeHtmlPatient;

    @FXML
    void initialize() {
        loadDoctor.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) { loadDoctor(); }
        });
        loadPatient.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                loadPatient();
            }
        });

        addDoctor.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try{
                    if(addDocSurname.getText().isEmpty() || addDocName.getText().isEmpty() || addDocMiddle.getText().isEmpty() ||
                            addDocSpecialty.getText().isEmpty() || addDocNote.getText().isEmpty()) throw new EmptyPersonException("Доктор");
                    int newId = doctorList.getItems().size()+1;
                    Doctor newDoctor = new Doctor(newId, addDocSurname.getText(), addDocName.getText(),
                            addDocMiddle.getText(), addDocSpecialty.getText(), addDocNote.getText());
                    doctorList.getItems().add(newDoctor);
                    addDocSurname.clear();
                    addDocName.clear();
                    addDocMiddle.clear();
                    addDocSpecialty.clear();
                    addDocNote.clear();

                    getAlert("Врач", "Врач успешно добавлен!");
                }
                catch (EmptyPersonException e) {e.getAlert();}
            }
        });
        addPatient.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try{
                    int newId = patientList.getItems().size()+1;
                    if(addPatSurname.getText().isEmpty() || addPatName.getText().isEmpty() || addPatMiddle.getText().isEmpty() ||
                            addPatDiagnos.getText().isEmpty() || addPatNote.getText().isEmpty()) throw new EmptyPersonException("Пациент");
                    System.out.println(addPatSurname.getText());
                    Patient newPatient = new Patient(newId, addPatSurname.getText(), addPatName.getText(),
                            addPatMiddle.getText(), addPatDiagnos.getText(), addPatNote.getText());
                    patientList.getItems().add(newPatient);
                    addPatSurname.clear();
                    addPatName.clear();
                    addPatMiddle.clear();
                    addPatDiagnos.clear();
                    addPatNote.clear();

                    getAlert("Добавление", "Пациент успешно добавлен!");
                }
                catch (EmptyPersonException e) {e.getAlert();}

            }
        });

        saveDoctor.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) { saveDoctor(); }
        });
        savePatient.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) { savePatient(); }
        });

        deleteDoctor.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                int index = doctorList.getSelectionModel().getSelectedIndex();
                doctorList.getItems().remove(index);
                doctorList.refresh();
                getAlert("Доктор", "Выбранный доктор успешно исключен из таблицы");
            }
        });
        deletePatient.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                int index = patientList.getSelectionModel().getSelectedIndex();
                patientList.getItems().remove(index);
                patientList.refresh();
                getAlert("Пациент", "Выбранный пациент успешно исключен из таблицы");
            }
        });

        makePdfDoctor.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                PdfReport report = new PdfReport(doctorList.getItems(), null, "../DataSrc/DoctorDataPdf");
                report.pdfSave();
                getAlert("PDF", "Отчет построен");
            }
        });
        makePdfPatient.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                PdfReport report = new PdfReport(null, patientList.getItems(), "../DataSrc/PatientDataPdf");
                report.pdfSave();
                getAlert("PDF", "Отчет построен");
            }
        });

        makeHtmlDoctor.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                HtmlReport report = new HtmlReport(doctorList.getItems(), null, "../DataSrc/DoctorDataHtml");
                report.saveHtml();
                getAlert("HTML", "Отчет построен");
            }
        });
        makeHtmlPatient.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                HtmlReport report = new HtmlReport(null, patientList.getItems(), "../DataSrc/PatientDataHtml");
                report.saveHtml();
                getAlert("HTML", "Отчет построен");
            }
        });

        doctorList.setEditable(true);
        patientList.setEditable(true);

        doctorId.setCellValueFactory(new PropertyValueFactory<Doctor, Integer>("id"));

        docSurname.setCellValueFactory(new PropertyValueFactory<Doctor, String>("surname"));
        docSurname.setEditable(true);
        docSurname.setCellFactory(TextFieldTableCell.forTableColumn());
        docSurname.setOnEditCommit(e -> e.getRowValue().setSurname(e.getNewValue()));

        docName.setCellValueFactory(new PropertyValueFactory<Doctor, String>("name"));
        docName.setEditable(true);
        docName.setCellFactory(TextFieldTableCell.forTableColumn());
        docName.setOnEditCommit(e -> e.getRowValue().setName(e.getNewValue()));

        docMiddleName.setCellValueFactory(new PropertyValueFactory<Doctor, String>("middleName"));
        docMiddleName.setEditable(true);
        docMiddleName.setCellFactory(TextFieldTableCell.forTableColumn());
        docMiddleName.setOnEditCommit(e -> e.getRowValue().setMiddleName(e.getNewValue()));

        docSpecialty.setCellValueFactory(new PropertyValueFactory<Doctor, String>("specialty"));
        docSpecialty.setEditable(true);
        docSpecialty.setCellFactory(TextFieldTableCell.forTableColumn());
        docSpecialty.setOnEditCommit(e -> e.getRowValue().setSpecialty(e.getNewValue()));

        docNote.setCellValueFactory(new PropertyValueFactory<Doctor, String>("note"));
        docNote.setEditable(true);
        docNote.setCellFactory(TextFieldTableCell.forTableColumn());
        docNote.setOnEditCommit(e -> e.getRowValue().setNote(e.getNewValue()));

        patientId.setCellValueFactory(new PropertyValueFactory<Patient, Integer>("id"));

        patientSurname.setCellValueFactory(new PropertyValueFactory<Patient, String>("surname"));
        patientSurname.setEditable(true);
        patientSurname.setCellFactory(TextFieldTableCell.forTableColumn());
        patientSurname.setOnEditCommit(e -> e.getRowValue().setSurname(e.getNewValue()));

        patientName.setCellValueFactory(new PropertyValueFactory<Patient, String>("name"));
        patientName.setEditable(true);
        patientName.setCellFactory(TextFieldTableCell.forTableColumn());
        patientName.setOnEditCommit(e -> e.getRowValue().setName(e.getNewValue()));

        patientMiddleName.setCellValueFactory(new PropertyValueFactory<Patient, String>("middleName"));
        patientMiddleName.setEditable(true);
        patientMiddleName.setCellFactory(TextFieldTableCell.forTableColumn());
        patientMiddleName.setOnEditCommit(e -> { e.getRowValue().setMiddleName(e.getNewValue()); });

        patientDiagnos.setCellValueFactory(new PropertyValueFactory<Patient, String>("diagnos"));
        patientDiagnos.setEditable(true);
        patientDiagnos.setCellFactory(TextFieldTableCell.forTableColumn());
        patientDiagnos.setOnEditCommit(e -> e.getRowValue().setDiagnos(e.getNewValue()));

        patientNote.setCellValueFactory(new PropertyValueFactory<Patient, String>("note"));
        patientNote.setEditable(true);
        patientNote.setCellFactory(TextFieldTableCell.forTableColumn());
        patientNote.setOnEditCommit(e -> e.getRowValue().setNote(e.getNewValue()));

        Object mutex = new Object();
        MyThread threadReader = new MyThread(mutex,1);
        MyThread threadEditer = new MyThread(mutex, 2);
        MyThread threadReporter = new MyThread(mutex, 3);

        threadReader.start();
        try { threadReader.join(); } catch (InterruptedException e) { e.printStackTrace(); }

        threadEditer.setDoctorList(threadReader.getDoctorList());
        threadEditer.start();
        try { threadEditer.join(); } catch (InterruptedException e) { e.printStackTrace(); }
        doctorList.setItems(threadReader.getDoctorList());

        threadReporter.setDoctorList(doctorList.getItems());
        threadReporter.start();
        try { threadReporter.join(); } catch (InterruptedException e) { e.printStackTrace(); }

    }

    public void loadDoctor( ) {

        final FileChooser fileChooser = new FileChooser();
        doctorSrc = fileChooser.showOpenDialog(new Stage());
        doctorList.getItems().clear();
        if (getFileExtension(doctorSrc).equals("csv"))
        {
            try {
                //создаем объект FileReader для объекта File
                FileReader fr = new FileReader(doctorSrc);
                //создаем BufferedReader с существующего FileReader для построчного считывания
                BufferedReader reader = new BufferedReader(fr);
                // считаем сначала первую строку
                String line = reader.readLine();
                while (line != null) {
                    doctorData.add(new Doctor(line.split(";")));
                    line = reader.readLine();
                }
                doctorList.setItems(doctorData);
            } catch (IOException e) { e.printStackTrace(); }
        }
        else
        {
            if(getFileExtension(doctorSrc).equals("xml"))
            {
                XmlParser newParser = new XmlParser(doctorSrc);
                newParser.parseDoctor();
                doctorList.setItems(newParser.getDoctorList());
            }
        }
    }

    public void loadPatient( ) {
        final FileChooser fileChooser = new FileChooser();
        patientSrc = fileChooser.showOpenDialog(new Stage());
        patientList.getItems().clear();
        if (getFileExtension(patientSrc).equals("csv")) {
            try {
                //создаем объект FileReader для объекта File
                FileReader fr = new FileReader(patientSrc);
                //создаем BufferedReader с существующего FileReader для построчного считывания
                BufferedReader reader = new BufferedReader(fr);
                // считаем сначала первую строку
                String line = reader.readLine();
                while (line != null) {
                    patientData.add(new Patient(line.split(";")));
                    line = reader.readLine();
                }
                patientList.setItems(patientData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            if(getFileExtension(patientSrc).equals("xml")) {
                XmlParser newParser = new XmlParser(patientSrc);
                newParser.parsePatient();
                patientList.setItems(newParser.getPatientList());
                System.out.println("Hello");
            }
        }
    }

    public void saveDoctor( ) {
        try {
            if (doctorSrc == null) throw new NoFileException("докторов");
            if (getFileExtension(doctorSrc).equals("csv")) {
                FileWriter fw = new FileWriter(this.doctorSrc, false);
                int newId = 1;
                for (Doctor doctor : doctorList.getItems()) {
                    String str = newId + doctor.getString() + "\n";
                    fw.write(str);
                    fw.flush();
                    newId++;
                }
                this.getAlert("Изменение", "Изменения успешно сохрнены в исходный файл");
            } else {
                if (getFileExtension(doctorSrc).equals("xml")) {
                    XmlSaver saver = new XmlSaver(doctorList.getItems(),null, doctorSrc);
                    saver.saveDoctor();
                    this.getAlert("Изменение", "Изменения успешно сохрнены в исходный файл");
                }
            }
        }
        catch (Exception e) {e.printStackTrace();}
    }

    public void savePatient( ) {
        try {
            if(patientSrc == null) throw new NoFileException("пациентов");
            if (getFileExtension(patientSrc).equals("csv")) {
                FileWriter fw = new FileWriter(this.patientSrc, false);
                int newId = 1;
                for (Patient patient : patientList.getItems()) {
                    String str = newId + patient.getString() + "\n";
                    fw.write(str);
                    fw.flush();
                    newId++;
                }
                this.getAlert("Изменение", "Изменения успешно сохрнены в исходный файл");
            }
            if (getFileExtension(patientSrc).equals("xml")) {
                XmlSaver saver = new XmlSaver(null,patientList.getItems(),patientSrc);
                saver.savePatient();
                this.getAlert("Изменение", "Изменения успешно сохрнены в исходный файл");
            }
        }
        catch (Exception e) {e.printStackTrace();}
    }

    public void getAlert(String header,String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(header);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        // если в имени файла есть точка и она не является первым символом в названии файла
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            // то вырезаем все знаки после последней точки в названии файла, то есть ХХХХХ.txt -> txt
            return fileName.substring(fileName.lastIndexOf(".")+1);
            // в противном случае возвращаем заглушку, то есть расширение не найдено
        else return "";
    }
}
